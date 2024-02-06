fn main() -> Result<(), Box<dyn std::error::Error>> {




    // // Specify the .proto file path
    // let proto_file = "proto/orderbook.proto";

    // // Configure prost-build
    // let mut config = prost_build::Config::new();
    // config.type_attribute(".", "#[derive(Summary)]");
    // config.compile_protos(&[proto_file], &["proto/"])?;





    tonic_build::configure()
        .compile(&["proto/orderbook.proto"], &["proto"])
        //.ord(true)
        .unwrap();

    tonic_build::compile_protos("proto/orderbook.proto")?;



    Ok(())
}