use std::collections::HashMap;
use std::sync::{Arc, Mutex};

use tonic::{transport::Server, Request, Response, Status};

pub mod orderbook {
    tonic::include_proto!("orderbook");
}

use orderbook::orderbook_aggregator_server::{OrderbookAggregator, OrderbookAggregatorServer};
use orderbook::{Empty, Level, Summary, MySummaryResponse};

#[derive(Default)]
pub struct OrderbookAggregatorService {
    orderbook_updates: Arc<Mutex<HashMap<String, Level>>>,
}

#[tonic::async_trait]
impl OrderbookAggregator for OrderbookAggregatorService {
    type BookSummaryStream = tokio_stream::wrappers::ReceiverStream<Result<Summary, Status>>;


    // async fn book_summary_string(
    //     &self,
    //     _request: Request<Empty>,
    // ) -> String {
    //     "{\"test\", \"t1\"}".to_string()
    // }


    async fn book_summary(
        &self,
        _request: Request<Empty>,
    ) -> Result<Response<Self::BookSummaryStream>, Status> {
        let orderbook_updates = self.orderbook_updates.clone();

        let (tx, rx) = tokio::sync::mpsc::channel(100);

        tokio::spawn(async move {
            let mut bids: Vec<Level> = Vec::new();
            let mut asks: Vec<Level> = Vec::new();

            if let Ok(orderbook_updates) = orderbook_updates.lock() {
                let levels: Vec<&Level> = orderbook_updates.values().collect();
                //levels.sort_by(|a, b| a.price.partial_cmp(&b.price).unwrap());

                asks = levels.iter().take(10).map(|&level| level.clone()).collect();
                bids = levels.iter().rev().take(10).map(|&level| level.clone()).collect();

            }

            let spread = match (bids.first(), asks.first()) {
                (Some(bid), Some(ask)) => ask.price - bid.price,
                _ => 0.0,
            };

            let summary = Summary {
                spread,
                bids,
                asks,
            };

            tx.send(Ok(summary)).await.unwrap();
        });

        let stream = tokio_stream::wrappers::ReceiverStream::new(rx);

        Ok(Response::new(stream))
    }



    async fn update_order(
        &self,
        request: Request<Level>,
    ) -> Result<Response<Level>, Status> {
        let order_request = request.into_inner();

        let exchange = order_request.exchange.clone();
        let price = order_request.price;
        let amount = order_request.amount;
        
        let level = Level {
            exchange: exchange.clone(),
            price,
            amount,
        };
        
        if order_request.exchange == "C".to_string(){
            let mut all_orderbook = self.orderbook_updates.lock().unwrap();
            all_orderbook.clear();
        }else{
            if let Ok(mut orderbook_updates) = self.orderbook_updates.lock() {
                orderbook_updates.insert(exchange, level.clone());
            }else{
                println!("{:?} wasn't added to server.", level);
            }
        }
        
        Ok(Response::new(level))
    }



    async fn get_my_summary(
        &self,
        _request: Request<Empty>,
    ) -> Result<Response<MySummaryResponse>, Status> {
        let orderbook_updates = self.orderbook_updates.clone();
        let mut bids: Vec<Level> = Vec::new();
        let mut asks: Vec<Level> = Vec::new();

        if let Ok(orderbook_updates) = orderbook_updates.lock() {
            let mut levels: Vec<&Level> = orderbook_updates.values().collect();
            levels.sort_by(|a, b| a.exchange.partial_cmp(&b.exchange).unwrap());

            asks = levels.iter().take(10).map(|&level| level.clone()).collect();
            bids = levels.iter().rev().take(10).map(|&level| level.clone()).collect();

        }

        let spread = match (bids.first(), asks.first()) {
            (Some(bid), Some(ask)) => ask.price - bid.price,
            _ => 0.0,
        };


        let mut json_str = String::from("{");
        json_str.push_str(r#""spread": "#);
        json_str.push_str(&spread.to_string());
        json_str.push_str(", \"bids\": [");
        
        for (i, bid) in bids.iter().enumerate() {
            json_str.push_str("{");
            json_str.push_str("\"exchange\": \"");
            let trimmed_string = bid.exchange.to_string();
            let trimmed_string = trimmed_string.splitn(2, '_').nth(1).unwrap_or("");
            json_str.push_str(trimmed_string);
            json_str.push_str("\", \"price\": ");
            json_str.push_str(&bid.price.to_string());
            json_str.push_str(", \"amount\": ");
            json_str.push_str(&bid.amount.to_string());
            json_str.push_str("}");
        
            if i < bids.len() - 1 {
                json_str.push_str(", ");
            }
        }
        
        json_str.push_str("], \"asks\": [");
        
        for (i, ask) in asks.iter().enumerate() {
            json_str.push_str("{");
            json_str.push_str("\"exchange\": \"");
            let trimmed_string = ask.exchange.to_string();
            let trimmed_string = trimmed_string.splitn(2, '_').nth(1).unwrap_or("");
            json_str.push_str(trimmed_string);
            json_str.push_str("\", \"price\": ");
            json_str.push_str(&ask.price.to_string());
            json_str.push_str(", \"amount\": ");
            json_str.push_str(&ask.amount.to_string());
            json_str.push_str("}");
        
            if i < asks.len() - 1 {
                json_str.push_str(", ");
            }
        }
        
        json_str.push(']');
        json_str.push('}');

        let mut response = MySummaryResponse::default();
        response.summaries = json_str;

        Ok(Response::new(response))

    }

}






#[tokio::main]
async fn main() -> Result<(), Box<dyn std::error::Error>> {
    let addr = "[::1]:50051".parse()?;
    let orderbook_aggregator_service = OrderbookAggregatorService::default();

    println!("Server listening on {}", addr);

    Server::builder()
        .add_service(OrderbookAggregatorServer::new(orderbook_aggregator_service))
        .serve(addr)
        .await?;

    Ok(())
}