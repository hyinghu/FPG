#![allow(non_snake_case)]
/// to run:
/// 1: go to rocket_server -> cargo run
/// 2: cargo run from PARTIES number of terminals
use curv::{
    arithmetic::traits::Converter,
    cryptographic_primitives::{
        proofs::sigma_dlog::DLogProof, secret_sharing::feldman_vss::VerifiableSS,
    },
    elliptic::curves::secp256_k1::{FE, GE},
    elliptic::curves::traits::{ECPoint, ECScalar},
    BigInt,
};
use multi_party_ecdsa::protocols::multi_party_ecdsa::gg_2018::party_i::{
    KeyGenBroadcastMessage1, KeyGenDecommitMessage1, Keys, Parameters,
};
use paillier::EncryptionKey;
use reqwest::Client;
use std::{env, fs, time};

mod common;
use common::{
    aes_decrypt, aes_encrypt, broadcast, poll_for_broadcasts, poll_for_p2p, postb, sendp2p, Params,
    PartySignup, AEAD, AES_KEY_BYTES_LEN,
};


use multi_party_ecdsa::bw::account::{MasterAccount, MasterKeyEntropy};
use multi_party_ecdsa::bw::mnemonic::Mnemonic;
use bitcoin::{
    network::constants::Network,
    util::bip32::{ChildNumber, ExtendedPrivKey, ExtendedPubKey},
    PrivateKey, PublicKey,
};

use magic_crypt::{new_magic_crypt, MagicCryptTrait};

use std::fs::OpenOptions;
use std::io::Write;

fn main() {
    let p_data = fs::read_to_string("params.json")
        .expect("Unable to read params, make sure config file is present in the same folder ");
    let p_params: Params = serde_json::from_str(&p_data).unwrap();
    let p_PARTIES: u16 = p_params.parties.parse::<u16>().unwrap();
    let p_THRESHOLD: u16 = p_params.threshold.parse::<u16>().unwrap();
    if p_THRESHOLD >= p_PARTIES || p_PARTIES <= 0 || p_PARTIES > 5 || p_THRESHOLD <= 0{
        println!("{}", "params.json has incorrect settings.");
        return;
    }else{
        // println!("parties: {}", p_PARTIES);
        // println!("threshold: {}", p_THRESHOLD);
    }


    if env::args().len() == 2 {
        //generate random words
        let s = Mnemonic::generate_random_words2(env::args().nth(1).unwrap());
        //fs::write("./testData.txt", s.clone()).expect("Unable to write file for ");
        let mut ff = OpenOptions::new()
            .create(true)
            .write(true)
            .append(true)
            .open("./testDataMN.txt")
            .unwrap();

            //ff.write(env::args().nth(1).as_bytes()).expect("write failed for temp token");
            //ff.write(s.clone().as_bytes()).expect("write failed for encryped string");

            writeln!(ff, "{}", env::args().nth(1).unwrap()).ok();
            writeln!(ff, "{}", s.clone()).ok();
            writeln!(ff,"======================================").ok();

        println!("{}", s);

        // let mc = new_magic_crypt!("FIPASSCODE", 256);
        // let base64 = mc.decrypt_base64_to_string(s).unwrap();
        // println!("{}", base64);

        return;
    }



    if env::args().nth(6).is_some() {
        panic!("too many arguments")
    }
    if env::args().nth(3).is_none() {
        panic!("too few arguments")
    }


    //read parameters:
    let data = fs::read_to_string("params.json")
        .expect("Unable to read params, make sure config file is present in the same folder ");
    let params: Params = serde_json::from_str(&data).unwrap();
    let PARTIES: u16 = params.parties.parse::<u16>().unwrap();
    let THRESHOLD: u16 = params.threshold.parse::<u16>().unwrap();

    let client = Client::new();

    // delay:
    let delay = time::Duration::from_millis(25);
    let params = Parameters {
        threshold: THRESHOLD,
        share_count: PARTIES,
    };

    //signup:
    let (party_num_int, uuid) = match signup(&client).unwrap() {
        PartySignup { number, uuid } => (number, uuid),
    };
    println!("number: {:?}, uuid: {:?}", party_num_int, uuid);

    // let party_keys = Keys::create(party_num_int as usize);
    //let n = BigInt::from(1855425871872_u64);





    let party_keys : Keys;
    if env::args().len() == 6 {
        //const PASSPHRASE: &str = "correct horse battery staple";   
        //let words = "announce damage viable ticket engage curious yellow ten clock finish burden orient faculty rigid smile host offer affair suffer slogan mercy another switch park";
        let PASSPHRASE: &str = &env::args().nth(3).unwrap();   
        let words = &env::args().nth(4).unwrap();
        // println!("{}", PASSPHRASE);
        // println!("{}", words);

        let mnemonic = Mnemonic::from_str(&words.replace("_", " ")).unwrap();
        let master = MasterAccount::from_mnemonic(&mnemonic, 0, Network::Bitcoin, PASSPHRASE, None).unwrap();

        // extract seed
        let seed = master.seed(Network::Bitcoin, PASSPHRASE).unwrap();
        let st = format!("{:X?}", seed.0.as_slice());
        let mut sb: String = "".to_string();
        for x in seed.0.as_slice() {
            //println!("tttt  {:X?}", x);
            sb = sb + &format!("{:X?}", x);
        }
        // println!("Seed == {}", sb);

        let s: String = sb.to_string();
        party_keys = Keys::createFromHex(&s , party_num_int as usize);   
    }else if env::args().len() == 5 {
        let args: Vec<String> = env::args().collect();

        // let num = &args[3];
        // let number: u64 = num.parse::<u64>().unwrap();
        // let i: BigInt = BigInt::from(number);

        let i = BigInt::from_hex(&args[3]);
        if i.is_ok(){
            party_keys = Keys::createFromBigInt(&i.unwrap(), party_num_int as usize); 
        }
        else{
            panic!("Hex error");
        }
    }else{
       party_keys = Keys::create(party_num_int as usize);
    }
    //println!("party_keys = {:?}\n\n", party_keys);




    let (bc_i, decom_i) = party_keys.phase1_broadcast_phase3_proof_of_correct_key();

    // send commitment to ephemeral public keys, get round 1 commitments of other parties
    assert!(broadcast(
        &client,
        party_num_int,
        "round1",
        serde_json::to_string(&bc_i).unwrap(),
        uuid.clone()
    )
    .is_ok());
    let round1_ans_vec = poll_for_broadcasts(
        &client,
        party_num_int,
        PARTIES,
        delay,
        "round1",
        uuid.clone(),
    );

    let mut bc1_vec = round1_ans_vec
        .iter()
        .map(|m| serde_json::from_str::<KeyGenBroadcastMessage1>(m).unwrap())
        .collect::<Vec<_>>();

    bc1_vec.insert(party_num_int as usize - 1, bc_i);

    // send ephemeral public keys and check commitments correctness
    println!("round2 >>>> {:?}\n\n", "end ephemeral public keys and check commitments correctness");

    assert!(broadcast(
        &client,
        party_num_int,
        "round2",
        serde_json::to_string(&decom_i).unwrap(),
        uuid.clone()
    )
    .is_ok());
    let round2_ans_vec = poll_for_broadcasts(
        &client,
        party_num_int,
        PARTIES,
        delay,
        "round2",
        uuid.clone(),
    );

    println!("round2 done >>>> {:?}\n\n", ">>>>");

    let mut j = 0;
    let mut point_vec: Vec<GE> = Vec::new();
    let mut decom_vec: Vec<KeyGenDecommitMessage1> = Vec::new();
    let mut enc_keys: Vec<Vec<u8>> = Vec::new();
    for i in 1..=PARTIES {
        if i == party_num_int {
            point_vec.push(decom_i.y_i);
            decom_vec.push(decom_i.clone());
        } else {
            let decom_j: KeyGenDecommitMessage1 = serde_json::from_str(&round2_ans_vec[j]).unwrap();
            point_vec.push(decom_j.y_i);
            decom_vec.push(decom_j.clone());
            let key_bn: BigInt = (decom_j.y_i.clone() * party_keys.u_i).x_coor().unwrap();
            let key_bytes = BigInt::to_bytes(&key_bn);
            let mut template: Vec<u8> = vec![0u8; AES_KEY_BYTES_LEN - key_bytes.len()];
            template.extend_from_slice(&key_bytes[..]);
            enc_keys.push(template);
            j = j + 1;
        }
    }

    let (head, tail) = point_vec.split_at(1);
    let y_sum = tail.iter().fold(head[0], |acc, x| acc + x);

    let (vss_scheme, secret_shares, _index) = party_keys
        .phase1_verify_com_phase3_verify_correct_key_phase2_distribute(
            &params, &decom_vec, &bc1_vec,
        )
        .expect("invalid key");

    //////////////////////////////////////////////////////////////////////////////
    println!("round3>>>> {:?}\n\n", "----");

    let mut j = 0;
    for (k, i) in (1..=PARTIES).enumerate() {
        if i != party_num_int {
            // prepare encrypted ss for party i:
            let key_i = &enc_keys[j];
            let plaintext = BigInt::to_bytes(&secret_shares[k].to_big_int());
            let aead_pack_i = aes_encrypt(key_i, &plaintext);
            assert!(sendp2p(
                &client,
                party_num_int,
                i,
                "round3",
                serde_json::to_string(&aead_pack_i).unwrap(),
                uuid.clone()
            )
            .is_ok());
            j += 1;
        }
    }

    let round3_ans_vec = poll_for_p2p(
        &client,
        party_num_int,
        PARTIES,
        delay,
        "round3",
        uuid.clone(),
    );

    let mut j = 0;
    let mut party_shares: Vec<FE> = Vec::new();
    for i in 1..=PARTIES {
        if i == party_num_int {
            party_shares.push(secret_shares[(i - 1) as usize]);
        } else {
            let aead_pack: AEAD = serde_json::from_str(&round3_ans_vec[j]).unwrap();
            let key_i = &enc_keys[j];
            let out = aes_decrypt(key_i, aead_pack);
            let out_bn = BigInt::from_bytes(&out[..]);
            let out_fe = ECScalar::from(&out_bn);
            party_shares.push(out_fe);

            j += 1;
        }
    }

    // round 4: send vss commitments
    println!("round4>>>> {:?}\n\n", "send vss commitments");

    assert!(broadcast(
        &client,
        party_num_int,
        "round4",
        serde_json::to_string(&vss_scheme).unwrap(),
        uuid.clone()
    )
    .is_ok());
    let round4_ans_vec = poll_for_broadcasts(
        &client,
        party_num_int,
        PARTIES,
        delay,
        "round4",
        uuid.clone(),
    );

    let mut j = 0;
    let mut vss_scheme_vec: Vec<VerifiableSS<GE>> = Vec::new();
    for i in 1..=PARTIES {
        if i == party_num_int {
            vss_scheme_vec.push(vss_scheme.clone());
        } else {
            let vss_scheme_j: VerifiableSS<GE> = serde_json::from_str(&round4_ans_vec[j]).unwrap();
            vss_scheme_vec.push(vss_scheme_j);
            j += 1;
        }
    }

    let (shared_keys, dlog_proof) = party_keys
        .phase2_verify_vss_construct_keypair_phase3_pok_dlog(
            &params,
            &point_vec,
            &party_shares,
            &vss_scheme_vec,
            party_num_int as usize,
        )
        .expect("invalid vss");

    // round 5: send dlog proof
    assert!(broadcast(
        &client,
        party_num_int,
        "round5",
        serde_json::to_string(&dlog_proof).unwrap(),
        uuid.clone()
    )
    .is_ok());
    let round5_ans_vec = poll_for_broadcasts(
        &client,
        party_num_int,
        PARTIES,
        delay,
        "round5",
        uuid.clone(),
    );

    let mut j = 0;
    let mut dlog_proof_vec: Vec<DLogProof<GE>> = Vec::new();
    for i in 1..=PARTIES {
        if i == party_num_int {
            dlog_proof_vec.push(dlog_proof.clone());
        } else {
            let dlog_proof_j: DLogProof<GE> = serde_json::from_str(&round5_ans_vec[j]).unwrap();
            dlog_proof_vec.push(dlog_proof_j);
            j += 1;
        }
    }
    Keys::verify_dlog_proofs(&params, &dlog_proof_vec, &point_vec).expect("bad dlog proof");

    //save key to file:
    let paillier_key_vec = (0..PARTIES)
        .map(|i| bc1_vec[i as usize].e.clone())
        .collect::<Vec<EncryptionKey>>();

    let keygen_json = serde_json::to_string(&(
        party_keys,
        shared_keys,
        party_num_int,
        vss_scheme_vec,
        paillier_key_vec,
        y_sum.clone(),
    ))
    .unwrap();

    println!("{}", serde_json::to_string(&(y_sum,)).unwrap());

    // fs::write(env::args().nth(2).unwrap(), &keygen_json).expect("Unable to save !");


    // //encrypted file
    // let mc = new_magic_crypt!("FIPASSCODE", 256);
    // let base64 = mc.encrypt_str_to_base64(&keygen_json);
    // fs::write(env::args().nth(2).unwrap() + ".c", base64 + "::" + &serde_json::to_string(&(y_sum,)).unwrap()).expect("Unable to save encrypted key!");
    let mc = new_magic_crypt!(env::args().nth(4).unwrap(), 256);
    let base64 = mc.encrypt_str_to_base64(&keygen_json);
    fs::write(env::args().nth(2).unwrap(), base64).expect("Unable to save encrypted key!");


}

pub fn signup(client: &Client) -> Result<PartySignup, ()> {
    let key = "signup-keygen".to_string();

    let res_body = postb(&client, "signupkeygen", key).unwrap();
    serde_json::from_str(&res_body).unwrap()
}
