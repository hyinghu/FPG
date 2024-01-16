//
nohup /tmp/hyh/03/tool/geth1.9 --ethash.dagdir /tmp/hyh/03 --datadir /tmp/hyh/03 --nodiscover --networkid 2019 --port 30305 --rpc --rpcport 8547 --rpccorsdomain "*" --rpcaddr='0.0.0.0' --rpcapi db,personal,web3,eth,miner,net,txpool,debug --maxpeers 2 --miner.threads=1 --gasprice=0 --targetgaslimit "9000000000000" -allow-insecure-unlock &



loadScript('../FPG_SC_1.17.2.js');

var cseContractAbi = Output.contracts['FPG_BC_1.17.2_S0.5.10.sol:MasterDBOT'].abi;
var cseContract = eth.contract(JSON.parse(cseContractAbi)).at("0x6f2593d5d7cfce6b71e6ec2ddc5984bd112a7730");

var result = cseContract.getContractVersion.call();
web3.toUtf8(result);

var result = cseContract.getTokens.call();
var arrayLength = result.length;
for (var i = 0; i < arrayLength; i++) {
	// console.log(i, web3.toUtf8(result[i]));
}

var tokenList = result;
//=================================================================================================
//=================================================================================================
//=================================================================================================



loadScript('../MnT01.js')

var ContractAbi = Output.contracts['MnT01.sol:FPG_WnT'].abi
var Contract = eth.contract(JSON.parse(ContractAbi))

var createMnT = false; //vvvvvvvvvvvvvvvvvvvvv
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


var contractAddress = "0x6860481190f0687b8919ba7302bc45208920705f";
if (createMnT){
		var contractAddress =  eth.getTransactionReceipt(Instance.transactionHash).contractAddress;
}
var Contract = eth.contract(JSON.parse(ContractAbi)).at(contractAddress)
var transactionObject = {from: eth.accounts[0], gas:9000000000};

var namedAccounts = [{address:"0xc575379e3bb1f9fd9b41b540a35bddae44db6f13", name:"CSE_BERNARD2"},{address: "0x7534ee47c8b9ea5e74ad53f920f8f5fa6f662b1a", name:"CSE_BERNARD3"},{address: "0x83c7d073ba2f373bc428b1b6abf08a2e2372f904", name: "CSE_BERNARD4"}];








var WalletContractAbi = Output.contracts['FPG__MSW_T01.sol:MultiSigWallet'].abi
var WalletBinCode = "0x" + Output.contracts['FPG__MSW_T01.sol:MultiSigWallet'].bin
var result = Contract.addWalletSC.sendTransaction(web3.toHex("Wallet04***"), WalletBinCode, WalletContractAbi, transactionObject);
console.log("addWalletSC, waiting...");
for ( var h = 0; h < 3000000; h++);
var done = (eth.getTransactionReceipt(result) == null);
while(done){
	for ( var h = 0; h < 1000000; h++);
	var done = (eth.getTransactionReceipt(result) == null);
	console.log("Waiting...");
}
if (eth.getTransactionReceipt(result).status == 1){
	console.log("Done. New wallet contract created.");
}else{
	console.log("Sorry. New wallet contract wasn't created. It may already existed with same name.");
}




var TokenContractAbi = Output.contracts['FPG_PT_test01.sol:FPG_PT_test01'].abi
var TokenBinCode = "0x" + Output.contracts['FPG_PT_test01.sol:FPG_PT_test01'].bin
var result = Contract.addTokenSC.sendTransaction(web3.toHex("Token04***"), TokenBinCode, TokenContractAbi, transactionObject);
console.log("addTokenSC, waiting...");
for ( var h = 0; h < 3000000; h++);
var done = (eth.getTransactionReceipt(result) == null);
while(done){
	for ( var h = 0; h < 1000000; h++);
	var done = (eth.getTransactionReceipt(result) == null);
	console.log("Waiting...");
}
if (eth.getTransactionReceipt(result).status == 1){
	console.log("Done. New token was created.");
}else{
	console.log("Sorry. New token contract wasn't created. It may already existed with same name.");
}






//-----------------------------------------------------------------------
//var walletList = eth.accounts;
//select firm, trader, safedata from firmuserdata where safedata IS NOT NULL;

var activeWalletSC = Contract.activeWalletSC.call();
var walletContractAbi = Contract.getWalletSCABI.call(activeWalletSC);
var walletContract = eth.contract(JSON.parse(walletContractAbi));
var walletBinCode = Contract.getWalletSCBin.call(activeWalletSC);
var activeWalletSCVN = Contract.getWalletSCVN.call(activeWalletSC);
var walletDeployTransationObject = { from: eth.accounts[0], data: walletBinCode, gas: 500000000 };

for(var wl = 0; wl < namedAccounts.length; wl++){
		if (eth.accounts[0] == namedAccounts[wl].address){
			continue;
		}
		var bl = Contract.existWalletByName.call(namedAccounts[wl].name);
		if (bl){
			console.log("Wallet " + namedAccounts[wl].name + " has already existed.");
			continue;
		}

		var owners = [];
		owners.push(eth.accounts[0]);
		owners.push(namedAccounts[wl].address);
		var walletInstance = walletContract.new( owners, 1, walletDeployTransationObject);
		console.log("Creating wallet for ..." + namedAccounts[wl].address + ", txHash = " + walletInstance.transactionHash);
		for ( var h = 0; h < 3000000; h++);
		var done = (eth.getTransactionReceipt(walletInstance.transactionHash) == null);
		while(done){
			for ( var h = 0; h < 1000000; h++);
			var done = (eth.getTransactionReceipt(walletInstance.transactionHash) == null);
			console.log("Waiting...");
		}
		if (eth.getTransactionReceipt(walletInstance.transactionHash).status == 1){
			console.log("Wallet created!");
			var newWalletAddress = eth.getTransactionReceipt(walletInstance.transactionHash).contractAddress;
		}else{
			console.log("Sorry. Wallet wasn't created.");
			continue;
		}

		var linkedAddress = namedAccounts[wl].address;

		console.log("Registering wallet ... " + newWalletAddress + " for account : " + namedAccounts[wl].address);
		var createdWallet = Contract.createdWallet.sendTransaction(activeWalletSCVN, namedAccounts[wl].name, newWalletAddress, linkedAddress, transactionObject);
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
			console.log("!!!!!!!!Sorry. Wallet wasn't registed.!!!!!!!!!!");
		}
}



//-----------------------------------------------------------------------
var activeTokenSC = Contract.activeTokenSC.call();
var activeTokenSCVN = Contract.getTokenSCVN.call(activeTokenSC);
var tokenContractAbi = Contract.getTokenSCABI.call(activeTokenSC);
var tokenContract = eth.contract(JSON.parse(tokenContractAbi));
var tokenBinCode = Contract.getTokenSCBin.call(activeTokenSC);
var tokenDeployTransationObject = { from: eth.accounts[0], data: tokenBinCode, gas: 500000000 };


var result = cseContract.getTokenSymbolList.call();
var arrayLength = result[0].length;
for (var i = 0; i < arrayLength; i++) {
	 console.log(i, web3.toUtf8(result[0][i]), result[1][i]);
}

var tokenListWithDec = result;

for(var i = 0; i < tokenListWithDec[0].length; i++){
	console.log(i, web3.toUtf8(tokenListWithDec[0][i]), " : " + tokenListWithDec[1][i]);
	if (web3.toUtf8(tokenListWithDec[0][i]) == "system"){
		 console.log("IT WILL BE IGNORED......");
		 continue;
	}

	var b = Contract.existTokenByNS.call(tokenListWithDec[0][i], tokenListWithDec[0][i]);
	if (b){
		console.log("Token " + web3.toUtf8(tokenListWithDec[0][i]) + " has already existed.");

		var newTokenAddress = Contract.getActiveTokenAddressByNS.call(tokenListWithDec[0][i], tokenListWithDec[0][i])
		console.log("Getting new token..." + web3.toUtf8(tokenListWithDec[0][i]) + ", address = " + newTokenAddress);

	}

	if (!b){
				var pTotalSupply = Math.pow(2, 255);////////!!!!!!!!!!!!!!!!!!!!!!!!!!!!0???
				console.log("Creating token..." + web3.toUtf8(tokenListWithDec[0][i]));
				console.log("totalSupply = " + pTotalSupply / Math.pow(10, tokenListWithDec[1][i]));
				console.log("decial = " + tokenListWithDec[1][i]);
				var tokenInstance = tokenContract.new(pTotalSupply / Math.pow(10, tokenListWithDec[1][i]), tokenListWithDec[0][i], tokenListWithDec[0][i], tokenListWithDec[1][i], tokenDeployTransationObject);
				console.log("transactionHash = " + tokenInstance.transactionHash);
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

				console.log("Registering new token..." + web3.toUtf8(tokenListWithDec[0][i]) + ", address = " + newTokenAddress);
				var createdToken = Contract.createdToken.sendTransaction(activeTokenSCVN, tokenListWithDec[0][i], "0x0", newTokenAddress, tokenListWithDec[1][i], tokenListWithDec[0][i], transactionObject);
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
					console.log("Sorry. Token wasn't registed.");
				}
	}
	console.log("Restoring token balances...");

	var tokenContractNew = eth.contract(JSON.parse(tokenContractAbi)).at(newTokenAddress);
	var mainAccountInitialBalance = tokenContractNew.balanceOf(eth.accounts[0]);
	console.log("Current main account token balance is : " + mainAccountInitialBalance);


	var wL =  namedAccounts.length;
	var singleTokenList = [];
	singleTokenList.push(tokenListWithDec[0][i]);
	for(var j = 0; j < wL; j++) {
		var result = cseContract.balanceByWallet.call(namedAccounts[j].address, singleTokenList);
		console.log(namedAccounts[j].address);

		console.log("\t original:: " + web3.toUtf8(singleTokenList[0]) + " : " + result[0]);

		var cBalance = tokenContractNew.balanceOf(namedAccounts[j].address);
		console.log("\t New current:: " + web3.toUtf8(singleTokenList[0]) + " : " + cBalance);

		if (result[0] - cBalance > 0){
			console.log("       transfering " + (result[0] - cBalance) + " to > " + namedAccounts[j].address);
			var tranHash = tokenContractNew.transfer.sendTransaction( namedAccounts[j].address, (result[0] - cBalance), transactionObject);

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
}
