//on FPG_server
// nohup /tmp/hyh/FPG_BC1.8_1.9/tool/geth1.9 --ethash.dagdir /tmp/hyh/FPG_BC1.8_1.9 --datadir /tmp/hyh/FPG_BC1.8_1.9 --nodiscover --networkid 11112 --port 30326 --rpc --rpcport 8552 --rpccorsdomain "*" --rpcaddr='0.0.0.0' --rpcapi db,personal,web3,eth,miner,net,txpool,debug --maxpeers 2 --mine --miner.threads=1 --gasprice=0 --targetgaslimit "9000000000000" -allow-insecure-unlock &
// nohup /tmp/hyh/FPG_BC1.8_1.9/tool/geth1.9 --ethash.dagdir /tmp/hyh/FPG_BC1.8_1.9 --datadir /tmp/hyh/FPG_BC1.8_1.9 --nodiscover --networkid 11112 --port 30326 --rpc --rpcport 8552 --rpccorsdomain "*" --rpcaddr='0.0.0.0' --rpcapi db,personal,web3,eth,miner,net,txpool,debug --maxpeers 2 --mine --miner.threads=1 --gasprice=0 --targetgaslimit "9000000000000" -allow-insecure-unlock &


loadScript('./solc/MnT01.js')

var ContractAbi = Output.contracts['MnT01.sol:FPG_WnT'].abi
var Contract = eth.contract(JSON.parse(ContractAbi))

var createMnT = true; //vvvvvvvvvvvvvvvvvvvvv
if (createMnT){
		var BinCode = "0x" + Output.contracts['MnT01.sol:FPG_WnT'].bin
		var deployTransationObject = { from: eth.accounts[0], data: BinCode, gas: 500000000 };
		var Instance = Contract.new(deployTransationObject)

		console.log("creating MnT, waiting...");
		for ( var h = 0; h < 3000000; h++);
		var done = (eth.getTransactionReceipt(Instance.transactionHash) == null);
		while(done){
			for ( var h = 0; h < 1000000; h++);
			var done = (eth.getTransactionReceipt(Instance.transactionHash) == null);
			console.log("Waiting...");
		}
		if (eth.getTransactionReceipt(Instance.transactionHash).status == 1){
			console.log("Done");
		}else{
			console.log("Sorry");
		}
		//eth.getTransactionReceipt(Instance.transactionHash);
		eth.getTransactionReceipt(Instance.transactionHash).contractAddress;
}


var contractAddress = "0xb23ef8f7dc981361733a589635e6ad8ac74d3971";
if (createMnT){
		var contractAddress =  eth.getTransactionReceipt(Instance.transactionHash).contractAddress;
}
var Contract = eth.contract(JSON.parse(ContractAbi)).at(contractAddress)
var transactionObject = {from: eth.accounts[0], gas:9000000000};

//----------------------------------------------------------------------------------------------------------------------------------

var WalletContractAbi = Output.contracts['FPG__MSW_T01.sol:MultiSigWallet'].abi
var WalletBinCode = "0x" + Output.contracts['FPG__MSW_T01.sol:MultiSigWallet'].bin
var result = Contract.addWalletSC.sendTransaction(web3.toHex("Wallet01***"), WalletBinCode, WalletContractAbi, transactionObject);
console.log("addWalletSC, waiting...");
for ( var h = 0; h < 3000000; h++);
var done = (eth.getTransactionReceipt(result) == null);
while(done){
	for ( var h = 0; h < 1000000; h++);
	var done = (eth.getTransactionReceipt(result) == null);
	console.log("Waiting...");
}
if (eth.getTransactionReceipt(result).status == 1){
	console.log("Done");
}else{
	console.log("Sorry");
}




var TokenContractAbi = Output.contracts['FPG_PT_test01.sol:FPG_PT_test01'].abi
var TokenBinCode = "0x" + Output.contracts['FPG_PT_test01.sol:FPG_PT_test01'].bin
var result = Contract.addTokenSC.sendTransaction(web3.toHex("Token01***"), TokenBinCode, TokenContractAbi, transactionObject);
console.log("addTokenSC, waiting...");
for ( var h = 0; h < 3000000; h++);
var done = (eth.getTransactionReceipt(result) == null);
while(done){
	for ( var h = 0; h < 1000000; h++);
	var done = (eth.getTransactionReceipt(result) == null);
	console.log("Waiting...");
}
if (eth.getTransactionReceipt(result).status == 1){
	console.log("Done");
}else{
	console.log("Sorry");
}







//0x5442303100000000000000000000000000000000000000000000000000000000
//===========================================================================================================================================
loadScript('./solc/MnT01.js')

var contractAddress = "0xb23ef8f7dc981361733a589635e6ad8ac74d3971";

var ContractAbi = Output.contracts['MnT01.sol:FPG_WnT'].abi
var Contract = eth.contract(JSON.parse(ContractAbi)).at(contractAddress)
var transactionObject = {from: eth.accounts[0], gas:9000000000};


//Wallet---------------------------------------------------------------------------
var activeWalletSC = Contract.activeWalletSC.call();
if (activeWalletSC >= 1){

	var activeWalletSCVN = Contract.getWalletSCVN.call(activeWalletSC);
	var activeWallets = Contract.wallets.call(activeWalletSCVN, 0);
	var bl = (JSON.stringify(["0x", "0x", "0x"]) == JSON.stringify(activeWallets));
	if (bl){
		console.log("ActiveWallets is not empty");
	}
		console.log("Good to go....");


		var walletContractAbi = Contract.getWalletSCABI.call(activeWalletSC);
		var walletContract = eth.contract(JSON.parse(walletContractAbi));
		var walletBinCode = Contract.getWalletSCBin.call(activeWalletSC);
		var walletDeployTransationObject = { from: eth.accounts[0], data: walletBinCode, gas: 500000000 };



		var activeWalletSCVN_P = Contract.getWalletSCVN.call(activeWalletSC - 1);
		console.log(web3.toUtf8(activeWalletSCVN_P) + ": " + activeWalletSCVN_P);
		var i = 0;
		var activeWallets_P = Contract.wallets.call(activeWalletSCVN_P, i++);
		while(JSON.stringify(["0x", "0x", "0x"]) != JSON.stringify(activeWallets_P)){
			console.log(web3.toUtf8(activeWallets_P[0]));
			console.log(activeWallets_P);
			var b = Contract.existWalletByName.call(activeWallets_P[0])
			if (b){
				console.log("Wallet " + web3.toUtf8(activeWallets_P[0]) + " has already existed.");
				var activeWallets_P = Contract.wallets.call(activeWalletSCVN_P, i++);
				continue;
			}
			var pWalletContractAbi = Contract.getWalletSCABI.call(activeWalletSC - 1);
			var pWalletContract = eth.contract(JSON.parse(pWalletContractAbi)).at(activeWallets_P[1])
			var pOwners = [];
			var pRequired = pWalletContract.required.call();
			var j = 0;
			var pOwner = pWalletContract.owners.call(j++);
			while(pOwner != "0x"){
				pOwners.push(pOwner);
				var pOwner = pWalletContract.owners.call(j++);
			}
			//console.log(pOwners);
			//console.log(activeWallets.tokenAddress);

			var walletInstance = walletContract.new(pOwners, pRequired, walletDeployTransationObject);
			console.log("Creating wallet..." + web3.toUtf8(activeWallets_P[0]));
			for ( var h = 0; h < 3000000; h++);
			var done = (eth.getTransactionReceipt(walletInstance.transactionHash) == null);
			while(done){
				for ( var h = 0; h < 1000000; h++);
				var done = (eth.getTransactionReceipt(walletInstance.transactionHash) == null);
				console.log("Waiting...");
			}
			if (eth.getTransactionReceipt(walletInstance.transactionHash).status == 1){
				console.log("Wallet created!");
			}else{
				console.log("Sorry. Wallet wasn't created.");
				continue;
			}

			var linkedAddress = activeWallets_P[1];
			if (activeWallets_P[2] != "0x0000000000000000000000000000000000000000"){
					var linkedAddress = activeWallets_P[2];
			}
			console.log("Registering wallet..." + web3.toUtf8(activeWallets_P[0]));
			var createdWallet = Contract.createdWallet.sendTransaction(activeWalletSCVN, activeWallets_P[0], eth.getTransactionReceipt(walletInstance.transactionHash).contractAddress, linkedAddress, transactionObject);
			//var createdWallet = Contract.createdWallet.sendTransaction(activeWalletSCVN, activeWallets_P[0], eth.getTransactionReceipt(walletInstance.transactionHash).contractAddress, transactionObject);
			for ( var h = 0; h < 3000000; h++);
			var done = (eth.getTransactionReceipt(createdWallet) == null);
			while(done){
				for ( var h = 0; h < 1000000; h++);
				var done = (eth.getTransactionReceipt(createdWallet) == null);
				console.log("Waiting...");
			}
			if (eth.getTransactionReceipt(createdWallet).status == 1){
				console.log("Wallet registed!");
			}else{
				console.log("!!!!!!!!!!!!Sorry. Wallet wasn't registed.!!!!!!!!!!!!!!");
			}

			var activeWallets_P = Contract.wallets.call(activeWalletSCVN_P, i++);
		}
}else{
	console.log("activeWalletSC must be great or equal to 1");
}



//Token--------------------------------------------------------------------------
var activeTokenSC = Contract.activeTokenSC.call();
if (activeTokenSC >= 1){

	var activeTokenSCVN = Contract.getTokenSCVN.call(activeTokenSC);
	var activeTokens = Contract.tokens.call(activeTokenSCVN, 0);
	var bl = (JSON.stringify(["0x", "0x", "0x", "0", "0x"]) == JSON.stringify(activeTokens));
	if (bl){
		console.log("ActiveTokens is not empty");
	}
		console.log("Good to go....");


		var tokenContractAbi = Contract.getTokenSCABI.call(activeTokenSC);
		var tokenContract = eth.contract(JSON.parse(tokenContractAbi));
		var tokenBinCode = Contract.getTokenSCBin.call(activeTokenSC);
		var tokenDeployTransationObject = { from: eth.accounts[0], data: tokenBinCode, gas: 500000000 };



		var activeTokenSCVN_P = Contract.getTokenSCVN.call(activeTokenSC - 1);
		var pTokenContractAbi = Contract.getTokenSCABI.call(activeTokenSC - 1);

		console.log(web3.toUtf8(activeTokenSCVN_P) + ": " + activeTokenSCVN_P);
		var i = 0;
		var activeTokens_P = Contract.tokens.call(activeTokenSCVN_P, i++);
		while(JSON.stringify(["0x", "0x", "0x", "0", "0x"]) != JSON.stringify(activeTokens_P)){
			console.log(web3.toUtf8(activeTokens_P[0]));
			console.log(activeTokens_P);
			var b = Contract.existTokenByNS.call(activeTokens_P[0], activeTokens_P[4]);
			if (b){
				console.log("Token " + web3.toUtf8(activeTokens_P[0]) + " has already existed.");

				var newTokenAddress = Contract.getActiveTokenAddressByNS.call(activeTokens_P[0], activeTokens_P[4])
				console.log("Getting new token..." + web3.toUtf8(activeTokens_P[0]) + ", address = " + newTokenAddress);
				//var activeTokens_P = Contract.tokens.call(activeTokenSCVN_P, i++);
				//continue;
			}

			var pTokenContract = eth.contract(JSON.parse(pTokenContractAbi)).at(activeTokens_P[1])
			var pTotalSupply = pTokenContract.totalSupply.call();
			var pName = pTokenContract.name.call();
			var pSymbol = pTokenContract.symbol.call();
			var pDecimals = pTokenContract.decimals.call();

			if (!b){
						var tokenInstance = tokenContract.new(pTotalSupply / Math.pow(10, pDecimals), pName, pSymbol, pDecimals, tokenDeployTransationObject);
						console.log("Creating token..." + web3.toUtf8(activeTokens_P[0]));
						for ( var h = 0; h < 3000000; h++);
						var done = (eth.getTransactionReceipt(tokenInstance.transactionHash) == null);
						while(done){
							for ( var h = 0; h < 1000000; h++);
							var done = (eth.getTransactionReceipt(tokenInstance.transactionHash) == null);
							console.log("Waiting...");
						}
						if (eth.getTransactionReceipt(tokenInstance.transactionHash).status == 1){
							console.log("Token created!");
						}else{
							console.log("Sorry. Token wasn't created.");
							continue;
						}

						var newTokenAddress = eth.getTransactionReceipt(tokenInstance.transactionHash).contractAddress;

						console.log("Registering new token..." + web3.toUtf8(activeTokens_P[0]) + ", address = " + newTokenAddress);
						var createdToken = Contract.createdToken.sendTransaction(activeTokenSCVN, activeTokens_P[0], "0x0", newTokenAddress, pDecimals, pSymbol, transactionObject);
						for ( var h = 0; h < 3000000; h++);
						var done = (eth.getTransactionReceipt(createdToken) == null);
						while(done){
							for ( var h = 0; h < 1000000; h++);
							var done = (eth.getTransactionReceipt(createdToken) == null);
							console.log("Waiting...");
						}
						if (eth.getTransactionReceipt(createdToken).status == 1){
							console.log("Token registed!");
						}else{
							console.log("!!!!!!!!!!!Sorry. Token wasn't registed.!!!!!!!!!!!!");
						}
			}
			console.log("Restoring token balances...");
			//var activeWalletAddresses = Contract.getActiveWalletAddressList.call();


			//var tokenContractAbi = Contract.getTokenSCABI.call(activeTokenSC);
			var tokenContractNew = eth.contract(JSON.parse(tokenContractAbi)).at(newTokenAddress);
			var mainAccountInitialBalance = tokenContractNew.balanceOf(eth.accounts[0]);
			console.log("Current main account token balance is : " + mainAccountInitialBalance);


			var activeWalletSC = Contract.activeWalletSC.call();
			var activeWalletSCVN = Contract.getWalletSCVN.call(activeWalletSC);
			var j = 0;
			var activeWallets = [web3.toHex("Main Account"), eth.accounts[0], "0x0000000000000000000000000000000000000000"];
			activeWallets;
			var bl = false;
			while (!bl){
				console.log(web3.toUtf8(activeWallets[0]) + "'s balance : ");
				if (activeWallets[2] != "0x0000000000000000000000000000000000000000"){
						console.log("    !using original wallet address!");
						var pBalance = pTokenContract.balanceOf(activeWallets[2]);
						var targetAddress = activeWallets[2];
				}else{
						var pBalance = pTokenContract.balanceOf(activeWallets[1]);
						var targetAddress = activeWallets[1];
				}
				var cBalance = tokenContractNew.balanceOf(targetAddress);
				console.log("       pB = " + pBalance);
				console.log("       cB = " + cBalance);



				if (j > 0){
					if (pBalance - cBalance > 0){
						console.log("       transfering " + (pBalance - cBalance) + " to > " + targetAddress);
						var tranHash = tokenContractNew.transfer.sendTransaction( targetAddress, (pBalance - cBalance), transactionObject);
						for ( var h = 0; h < 3000000; h++);
						var done = (eth.getTransactionReceipt(tranHash) == null);
						while(done){
							for ( var h = 0; h < 1000000; h++);
							var done = (eth.getTransactionReceipt(tranHash) == null);
							console.log("Waiting...");
						}
						if (eth.getTransactionReceipt(tranHash).status == 1){
							console.log("Token balance was restored!");
						}else{
							console.log("Sorry. Token balance wasn't restored.");
						}
					}else{
						 console.log("       It was synced or you have to double-check it and do that mannaully.");
					}
				}


				var activeWallets = Contract.wallets.call(activeWalletSCVN, j++);
				var bl = (JSON.stringify(["0x", "0x", "0x"]) == JSON.stringify(activeWallets));
			}
			var pMABalance = pTokenContract.balanceOf(eth.accounts[0]);
			var cMABalance = tokenContractNew.balanceOf(eth.accounts[0]);
			console.log("       pMAB = " + pMABalance);
			console.log("After syncing: ");
			console.log("       cMAB = " + cMABalance);
			if (cMABalance > pMABalance){
					var burnMA = tokenContractNew.burnMA.sendTransaction((cMABalance - pMABalance), transactionObject);
					for ( var h = 0; h < 3000000; h++);
					var done = (eth.getTransactionReceipt(burnMA) == null);
					while(done){
						for ( var h = 0; h < 1000000; h++);
						var done = (eth.getTransactionReceipt(burnMA) == null);
						console.log("Waiting...");
					}
					if (eth.getTransactionReceipt(burnMA).status == 1){
						console.log("Master account was burned to sync the balance!");
					}else{
						console.log("Sorry. Master account was not burned successfully.");
					}
			}






			var activeTokens_P = Contract.tokens.call(activeTokenSCVN_P, i++);
		}
}else{
	console.log("activeTokenSC must be great or equal to 1");
}
