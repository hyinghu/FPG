// server.rs

// use common::SOCKET_PATH;
// use std::fs;
// use std::fs::PathExtensions;
// use std::net::pipe::UnixListener;
// use std::{Acceptor,Listener};

// mod common;




use std::fs::File;
use std::io;

use std::io::IoSlice;

use std::io::Read;
use std::io::Write;
use std::mem::{self, MaybeUninit};
use std::net::{Ipv4Addr, SocketAddr, SocketAddrV4, TcpStream};

use std::net::{Ipv6Addr, SocketAddrV6};

use std::num::NonZeroUsize;

//use std::os::unix::io::AsRawFd;

// use std::os::windows::io::AsRawSocket;
use std::str;
use std::thread;
use std::time::Duration;

use std::{env, fs};

// use winapi::shared::minwindef::DWORD;
// use winapi::um::handleapi::GetHandleInformation;
// use winapi::um::winbase::HANDLE_FLAG_INHERIT;

use socket2::MaybeUninitSlice;
use socket2::{Domain, Protocol, SockAddr, Socket, TcpKeepalive, Type};

const DATA: &[u8] = b"hello world";
fn any_ipv4() -> SockAddr {
    SocketAddrV4::new(Ipv4Addr::LOCALHOST, 0).into()
}
unsafe fn assume_init(buf: &[MaybeUninit<u8>]) -> &[u8] {
    &*(buf as *const [MaybeUninit<u8>] as *const [u8])
}

fn main() {
    // let socket = Path::new(SOCKET_PATH);

    // // Delete old socket if necessary
    // if socket.exists() {
    //     fs::unlink(&socket).unwrap();
    // }

    // // Bind to socket
    // let stream = match UnixListener::bind(&socket) {
    //     Err(_) => panic!("failed to bind socket"),
    //     Ok(stream) => stream,
    // };

    // println!("Server started, waiting for clients");

    // // Iterate over clients, blocks if no client available
    // for mut client in stream.listen().incoming() {
    //     println!("Client said: {}", client.read_to_string().unwrap());
    // }




    let listener = Socket::new(Domain::IPV4, Type::STREAM, None).unwrap();
    listener.bind(&any_ipv4()).unwrap();
    listener.listen(1).unwrap();

    let sender = Socket::new(Domain::IPV4, Type::STREAM, None).unwrap();
    sender.bind(&any_ipv4()).unwrap();
    sender.connect(&listener.local_addr().unwrap()).unwrap();

    let (receiver, _) = listener.accept().unwrap();

    sender.send(DATA).unwrap();

    const FIRST: &[u8] = b"!";
    assert_eq!(sender.send_out_of_band(FIRST).unwrap(), FIRST.len());
    // On macOS if no `MSG_OOB` is available it will return `EINVAL`, to prevent
    // this from happening we'll sleep to ensure the data is present.
    thread::sleep(Duration::from_millis(10));

    let mut buf = [MaybeUninit::new(1); DATA.len() + 1];
    let n = receiver.recv_out_of_band(&mut buf).unwrap();
    assert_eq!(n, FIRST.len());
    assert_eq!(unsafe { assume_init(&buf[..n]) }, FIRST);

    let n = receiver.recv(&mut buf).unwrap();
    assert_eq!(n, DATA.len());
    assert_eq!(unsafe { assume_init(&buf[..n]) }, DATA);
}