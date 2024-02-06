use tonic::{transport::Channel, Request};

pub mod orderbook {
    tonic::include_proto!("orderbook");
}

use orderbook::orderbook_aggregator_client::OrderbookAggregatorClient;
use orderbook::{Empty};

#[tokio::main]
async fn main() -> Result<(), Box<dyn std::error::Error>> {
    let channel = Channel::from_static("http://[::1]:50051")
        .connect()
        .await?;

    let mut client = OrderbookAggregatorClient::new(channel);




    // let request = Request::new(Empty {});
    // let mut stream = client.book_summary(request).await?.into_inner();
    // while let Some(summary) = stream.message().await? {
    //     println!("Received summary: {:?}", summary);
    // }




    // Make the GetMySummary request
    let request = Request::new(Empty {});
    let response = client.get_my_summary(request).await?;

    println!("\n\n{:?}\n\n", response);

    // // Process the response
    // let summaries = response.into_inner().summaries;

    // println!("\n\n{:?}\n\n", summaries);

    // for summary in summaries {
    //     println!("Spread: {}", summary.spread);
    //     for bid in summary.bids {
    //         println!("Exchange: {}, Price: {}, Amount: {}", bid.exchange, bid.price, bid.amount);
    //     }
    //     for ask in summary.asks {
    //         println!("Exchange: {}, Price: {}, Amount: {}", ask.exchange, ask.price, ask.amount);
    //     }
    // }
    
    Ok(())
}
