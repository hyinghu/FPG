
//use serde_json::Value;
use tonic::{transport::Channel, Request};

pub mod orderbook {
    tonic::include_proto!("orderbook");
}

use orderbook::orderbook_aggregator_client::OrderbookAggregatorClient;
use orderbook::{Level};

use tungstenite::connect;
use tungstenite::Message;

use std::thread;
use std::time::Duration;

use serde::{Deserialize, Serialize};
use std::fs;

async fn receive_orderbook_updates(channel: Channel, pairs: String) -> Result<(), Box<dyn std::error::Error>> {
    let mut client = OrderbookAggregatorClient::new(channel);

    let (mut bitstamp_socket, _) = connect("wss://ws.bitstamp.net")
    //    .await
        .expect("Failed to connect to Bitstamp WebSocket");

    let mut subscribe_msg = String::from(r#"{"event":"bts:subscribe","data":{"channel":"order_book_REPLACEME"}}"#);
    let new_pair = pairs.clone().replace("-", "").to_lowercase();
    subscribe_msg = subscribe_msg.replace("REPLACEME", &new_pair);

    println!("{}", subscribe_msg);

    bitstamp_socket.write_message(Message::Text(subscribe_msg.into())).unwrap();



    // let (mut coinbase_socket, _) = connect("wss://ws-feed.pro.coinbase.com")
    // //    .await
    //     .expect("Failed to connect to Coinbase WebSocket");
    
    // let mut subscribe_msg = String::from(r#"{
    //     "type": "subscribe",
    //     "product_ids": [
    //         "REPLACEME"
    //     ],
    //     "channels": [
    //         "level2"
    //     ]
    // }"#);
    // subscribe_msg = subscribe_msg.replace("REPLACEME", &pairs);

    // println!("{}", subscribe_msg);

    // coinbase_socket.write_message(Message::Text(subscribe_msg.into())).unwrap();


    let mut subscribe_msg = String::from(r#"wss://stream.binance.us:9443/ws/REPLACEME@depth20@100ms"#);
    let new_pair = pairs.clone().replace("-", "").to_lowercase();
    subscribe_msg = subscribe_msg.replace("REPLACEME", &new_pair);
    println!("{}", subscribe_msg);
    
    let (mut binance_socket, _) = connect(subscribe_msg)
    //    .await
        .expect("Failed to connect to Bitstamp WebSocket");




    #[derive(Debug, Deserialize, Serialize)]
    struct DataAgg {
        bids_coinbase: Vec<Vec<String>>,
        asks_coinbase: Vec<Vec<String>>,
        bids_bitstamp: Vec<Vec<String>>,
        asks_bitstamp: Vec<Vec<String>>,
        bids_merge: Vec<Vec<String>>,
        asks_merge: Vec<Vec<String>>,
    
    }   

    let mut data_agg = DataAgg {
        bids_coinbase: vec![],
        asks_coinbase: vec![],
        bids_bitstamp: vec![],
        asks_bitstamp: vec![],
        bids_merge: vec![],
        asks_merge: vec![],
    };


    loop{
        let msg = bitstamp_socket.read_message()?;
    
        thread::sleep(Duration::from_secs(2));

        match msg {
            Message::Text(text) => {
                // let truncated_text = if text.len() > 400 {
                //     &text[..400]
                // } else {
                //     &text
                // };
                // println!("Received message from Bitstamp: {}", truncated_text);

                //println!("{}", text);


                #[derive(Debug, Deserialize, Serialize)]
                struct Data {
                    bids: Vec<Vec<String>>,
                    asks: Vec<Vec<String>>,
                }

                let json_string: serde_json::Value = serde_json::from_str(&text).unwrap();
                let data_string = json_string["data"].to_string();

                let mut data: serde_json::Result<Data> = serde_json::from_str(&data_string);
                match data {
                    Ok(ref mut data) => {
                        let exchange_name = String::from("Bitstamp");

                        for ask in &mut data.asks {
                            ask.push(exchange_name.clone());
                        }

                        for bid in &mut data.bids {
                            bid.push(exchange_name.clone());
                        }

                        data.asks.sort_by(|a, b| a[0].partial_cmp(&b[0]).unwrap());
                        data_agg.asks_bitstamp = data.asks[..data.asks.len()].to_vec();
                        //println!("----{:?}", data_agg.asks_bitstamp);

                        data.bids.sort_by(|a, b| b[0].partial_cmp(&a[0]).unwrap());
                        data_agg.bids_bitstamp = data.bids[..data.bids.len()].to_vec();

                        // println!("Asks top 10: {:?}", &data.asks[..std::cmp::min(data.asks.len(), 10)]);
                        // println!("Bids top 10: {:?}\n\n\n", &data.bids[..std::cmp::min(data.bids.len(), 10)]);
                    }
                    Err(e) => {
                        println!("Error: {}", e);
                    }
                }

            }
            _ => (),
        }
    

        println!("===============================================");

        // let msg = coinbase_socket.read_message()?;

        // match msg {
        //     Message::Text(text) => {

        //         // let truncated_text = if text.len() > 200 {
        //         //     &text[..200]
        //         // } else {
        //         //     &text
        //         // };
        //         // println!("Received message from Coinbase: {}", truncated_text);


        //         if let Ok(json) = serde_json::from_str::<Value>(&text) {
        //             let msg_type = json.get("type").and_then(|value| value.as_str());
        //             match msg_type{
        //                 Some("snapshot") =>{
        //                     #[derive(Debug, Deserialize, Serialize)]
        //                     struct Data {
        //                         // #[serde(rename = "type")]
        //                         // _type: String,
        //                         // product_id: String,
        //                         asks: Vec<Vec<String>>,
        //                         bids: Vec<Vec<String>>,
        //                     }
                            
        //                     let mut data: serde_json::Result<Data> = serde_json::from_str(&text);
        //                     match data {
        //                         Ok(ref mut data) => {
        //                             let exchange_name = String::from("Coinbase");

        //                             for ask in &mut data.asks {
        //                                 ask.push(exchange_name.clone());
        //                             }

        //                             for bid in &mut data.bids {
        //                                 bid.push(exchange_name.clone());
        //                             }

        //                             data.asks.sort_by(|a, b| a[0].partial_cmp(&b[0]).unwrap());
        //                             data_agg.asks_coinbase = data.asks[..data.asks.len()].to_vec();

        //                             data.bids.sort_by(|a, b| b[0].partial_cmp(&a[0]).unwrap());
        //                             data_agg.bids_coinbase = data.bids[..data.bids.len()].to_vec();

        //                             // println!("Asks top 10: {:?}", &data.asks[..std::cmp::min(data.asks.len(), 10)]);
        //                             // println!("Bids top 10: {:?}", &data.bids[..std::cmp::min(data.bids.len(), 10)]);
        //                         }
        //                         Err(e) => {
        //                             println!("Error: {}", e);
        //                         }
        //                     }
        //                 }
        //                 Some("l2update") => {
        //                     #[derive(Debug, Deserialize, Serialize)]
        //                     struct Data {
        //                         changes: Vec<Vec<String>>,
        //                     }

        //                     println!("{}", text);


        //                     let mut data: serde_json::Result<Data> = serde_json::from_str(&text);
        //                     match data {
        //                         Ok(ref mut data) => {
        //                             let exchange_name = String::from("Coinbase");

                                    
        //                             for c in &mut data.changes {
        //                                 c.push(exchange_name.clone());

        //                                 //let mut data = vec!c;
        //                                 let removed_element = c.remove(0);
        //                                 if removed_element == "sell"{
        //                                     data_agg.asks_coinbase.push(c.to_vec());
        //                                 }
        //                                 if removed_element == "buy"{
        //                                     data_agg.bids_coinbase.push(c.to_vec());
        //                                 }

        //                                 println!("{:?}\n\n", c);
        //                             }
        //                         }
        //                         _ => ()
        //                     }
        //                 }
        //                 _ => (),
        //             }
                        
                    
        //         }
        //     }
        //     _ => (),
        // }



        let msg = binance_socket.read_message()?;

        match msg {
            Message::Text(text) => {
                // let truncated_text = if text.len() > 400 {
                //     &text[..400]
                // } else {
                //     &text
                // };
                // println!("Received message from Bitstamp: {}", truncated_text);

                //println!("{}", text);


                #[derive(Debug, Deserialize, Serialize)]
                struct Data {
                    bids: Vec<Vec<String>>,
                    asks: Vec<Vec<String>>,
                }

                let mut data: serde_json::Result<Data> = serde_json::from_str(&text);             
                match data {
                    Ok(ref mut data) => {
                        let exchange_name = String::from("Binance");

                        for ask in &mut data.asks {
                            ask.push(exchange_name.clone());
                        }

                        for bid in &mut data.bids {
                            bid.push(exchange_name.clone());
                        }

                        data.asks.sort_by(|a, b| a[0].partial_cmp(&b[0]).unwrap());
                        data_agg.asks_coinbase = data.asks[..data.asks.len()].to_vec();
                        //println!("asks----{:?}", data_agg.asks_coinbase);

                        data.bids.sort_by(|a, b| b[0].partial_cmp(&a[0]).unwrap());
                        data_agg.bids_coinbase = data.bids[..data.bids.len()].to_vec();
                        //println!("bids----{:?}", data_agg.bids_coinbase);

                        // println!("Asks top 10: {:?}", &data.asks[..std::cmp::min(data.asks.len(), 10)]);
                        // println!("Bids top 10: {:?}\n\n\n", &data.bids[..std::cmp::min(data.bids.len(), 10)]);
                    }
                    Err(e) => {
                        println!("Error: {}", e);
                    }
                }

            }
            _ => (),
        }



        let merged_bids: Vec<Vec<String>> = [&data_agg.bids_bitstamp[..], &data_agg.bids_coinbase[..]]
                .concat()
                .clone();
        let mut sorted_bids = merged_bids.clone();
        sorted_bids.sort_by(|a, b| b[0].partial_cmp(&a[0]).unwrap());

        data_agg.bids_merge = sorted_bids;


        let merged_asks: Vec<Vec<String>> = [&data_agg.asks_bitstamp[..], &data_agg.asks_coinbase[..]]
                .concat()
                .clone();
        let mut sorted_asks = merged_asks.clone();
        sorted_asks.sort_by(|a, b| a[0].partial_cmp(&b[0]).unwrap());

        data_agg.asks_merge = sorted_asks;

        // println!("Asks top 10: {:?}", &data_agg.asks_merge[..std::cmp::min(data_agg.asks_merge.len(), 10)]);
        // println!("Bids top 10: {:?}", &data_agg.bids_merge[..std::cmp::min(data_agg.bids_merge.len(), 10)]);
        let request = Request::new(Level{
            exchange: "C".to_string(),
            price: 0.,
            amount: 0.
        });                               
        let stream = client.update_order(request).await?.into_inner();   
        println!("Updated bid: {:?}", stream);


        let mut i = 0;
        for l in &data_agg.asks_merge[..std::cmp::min(data_agg.asks_merge.len(), 10)]{
            let level = Level{
                exchange: format!("A{}_{}", i, l[2].clone()),
                price: l[0].parse().unwrap(),
                amount: l[1].parse().unwrap()
            };
            i += 1;
            let request = Request::new(level);                               
            let stream = client.update_order(request).await?.into_inner();   
            println!("Updated ask: {:?}", stream);

        }

        let mut i = 0;
        for l in &data_agg.bids_merge[..std::cmp::min(data_agg.bids_merge.len(), 10)]{
            let level = Level{
                exchange: format!("B{}_{}", i, l[2].clone()),
                price: l[0].parse().unwrap(),
                amount: l[1].parse().unwrap()
            };
            i += 1;
            let request = Request::new(level);                               
            let stream = client.update_order(request).await?.into_inner();   
            println!("Updated bid: {:?}", stream);
        }
    }

}

#[tokio::main]
async fn main() -> Result<(), Box<dyn std::error::Error>> {
    // Connect to the gRPC server
    let channel = tonic::transport::Channel::from_static("http://[::1]:50051")
        .connect()
        .await?;


    println!("connected server.");

    let mut pair = String::new();
    if let Ok(contents) = fs::read_to_string("config/config.rpc") {
        for line in contents.lines() {
            println!("{}", line);

            let parts: Vec<&str> = line.splitn(2, ":").collect();
            if parts.len() == 2 && parts[0].trim() == "pair" {
                pair = parts[1].trim().to_string();
            }

        }
    } else {
        println!("Error reading the file");
    }


    // Receive order book updates and send them through the gRPC stream
    println!("Loading pair {} ...", pair);
    receive_orderbook_updates(channel, pair).await?;

    Ok(())
}
