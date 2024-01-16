pragma solidity ^0.5.2;
library SafeMath {
  function mul(uint256 a, uint256 b) internal pure returns (uint256) {
    uint256 c = a * b;
    assert(a == 0 || c / a == b);
    return c;
  }
  function div(uint256 a, uint256 b) internal pure returns (uint256) {
     assert(b > 0); // Solidity automatically throws when dividing by 0
    uint256 c = a / b;
     assert(a == b * c + a % b); // There is no case in which this doesn't hold
    return c;
  }
  
  function sub(uint256 a, uint256 b) internal pure returns (uint256) {
    assert(b <= a);
    return a - b;
  }
  function add(uint256 a, uint256 b) internal pure returns (uint256) {
    uint256 c = a + b;
    assert(c >= a);
    return c;
  }
}


contract Ownable {
  address private owner;
  constructor () public {
    //owner = msg.sender;
    owner = tx.origin;
  }
  function currentOwner() public /*constant*/ view returns (address) {
    return owner;
  }
  modifier onlyOwner() {
    require(tx.origin == owner);
    _;
  }
  function transferOwnership(address newOwner) public onlyOwner {
    require(newOwner != address(0));
    owner = newOwner;
  }
}




contract ERCMasterBasic {
    uint256 public totalSupply;
    function getTotalSupply() public /*constant*/ view  returns (uint256);
    function balanceOf(address who) public /*constant*/ view returns (uint256);
    function transfer(address from, address to, uint256 value) public returns (bool);
    function allowance(address to, address from) public /*constant*/ view returns (uint256);
    function transferFrom(address from, address to, uint256 value) public returns (bool);
    function approve(address from, address to, uint256 value) public returns (bool);
}


contract BasicMasterToken is ERCMasterBasic, Ownable  {
    using SafeMath for uint256;
    mapping(address => uint256) balances;
    mapping(address => uint256) freeze_balances;
    function transfer(address _from, address _to, uint256 _value) public onlyOwner returns (bool) {
        balances[_from] = balances[_from].sub(_value);
        balances[_to] = balances[_to].add(_value);
        return true;
    }
    
   
    
    //HYH
    function balanceOf(address _owner) public /*constant*/ view returns (uint256 balance) {
        return balances[_owner];
    }
    
    
    //HYH
    function freezeBalanceOf(address _owner) public /*constant*/ view returns (uint256 balance) {
        return freeze_balances[_owner];
    }
    
    
    
    function getTotalSupply() public /*constant*/ view returns (uint256 balanceTotal) {
        return totalSupply;
    }
}


contract StandartMasterToken is BasicMasterToken {
    
    
//  //HYH
//  function uint2str(uint256 i) internal pure returns (string){
//     if (i == 0) return "0";
//     uint j = i;
//     uint length;
//     while (j != 0){
//         length++;
//         j /= 10;
//     }
//     bytes memory bstr = new bytes(length);
//     uint k = length - 1;
//     while (i != 0){
//         bstr[k--] = byte(48 + i % 10);
//         i /= 10;
//     }
//     return string(bstr);
// }
    
  //event Print(uint256,uint256,uint256);    
  mapping (address => mapping (address => uint256)) allowed;
  function transferFrom(address _from, address _to, uint256 _value) public onlyOwner returns (bool) {
    //emit Print(11, balances[_from], _value);
      
    uint256 _allowance = allowed[_from][_to];
    balances[_to] = balances[_to].add(_value);
    balances[_from] = balances[_from].sub(_value);
    allowed[_from][_to] = _allowance.sub(_value);
    return true;
  }

  function approve(address _from, address _to, uint256 _value) public onlyOwner returns (bool) {
    //require((_value == 0) || (allowed[_from][_to] == 0), string(abi.encodePacked("allowed[_from][_to]==", uint2str(allowed[_from][_to]), ", _value == ", uint2str(_value))));
    //require((_value == 0) || (allowed[_from][_to] == 0), "approve() error");
    //emit Print(11, balances[_from], _value);
    
    require(((_value == 0) || (allowed[_from][_to] == 0))&&(balances[_from]>=_value));
    allowed[_from][_to] = _value;
    // if ((_value == 0) || (allowed[_from][_to] == 0)){
    //     allowed[_from][_to] = _value;
    // }
    // else{
    //     emit Print(_from,_to,allowed[_from][_to],_value);
    // }

    return true;
  }
  
  
  
  
  
  
  function allowance(address _from, address _to) public /*constant*/ view returns (uint256 remaining) {
    return allowed[_from][_to];
  }
}



contract MintableMasterToken is StandartMasterToken{
    event MintFinished();
    bool public mintingFinished = false;
    modifier canMint() {
        require(!mintingFinished);
        _;
    }
    
    //HYH
    //event Print22(uint256,uint256);
    function powOf(address _owner, uint256 p) public onlyOwner  returns (bool) {
        //emit Print22(balances[_owner], p);
        // if (p > 0)
        //  emit Print22(balances[_owner], 128);

        
        if (p < 0)
        {
            for (uint256 i = p; i < 0; i++){
                balances[_owner] = balances[_owner] / (10);
                freeze_balances[_owner] = freeze_balances[_owner] / (10);
            }
        }
        else
        {
            for (uint256 j = p; j > 0; j--){
                balances[_owner] = balances[_owner] * 10;
                freeze_balances[_owner] = freeze_balances[_owner] * (10);
            }
        }
            
        //emit Print22(balances[_owner], p);
        return true;
    }
    
    ////HYH
    //event Print11(uint256);
    function mint(address _to, uint256 _amount) public onlyOwner canMint returns (bool) {
        totalSupply = totalSupply.add(_amount);
        balances[_to] = balances[_to].add(_amount);
        
        //emit Print11(balances[_to]);
        
        return true;
    }

    
    
    //HYH
    function burn(address _to, uint256 _amount) public onlyOwner returns (bool) {
        totalSupply = totalSupply.sub(_amount);
        balances[_to] = balances[_to].sub(_amount);
        
        //emit Print(_to, balances[_to]);
        
        return true;
    }
    
    
    
    function freeze(address _to, uint256 _amount) public onlyOwner canMint returns (bool) {
        balances[_to] = balances[_to].sub(_amount);
        freeze_balances[_to] = freeze_balances[_to].add(_amount);
        return true;
    }
    function unFreeze(address _to, uint256 _amount) public onlyOwner returns (bool) {
        balances[_to] = balances[_to].add(_amount);
        freeze_balances[_to] = freeze_balances[_to].sub(_amount);
        return true;
    }
    function freezeBurn(address _to, uint256 _amount) public onlyOwner returns (bool) {
        freeze_balances[_to] = freeze_balances[_to].sub(_amount);
        return true;
    }
    function freezeMint(address _to, uint256 _amount) public onlyOwner returns (bool) {
        freeze_balances[_to] = freeze_balances[_to].add(_amount);
        return true;
    }

    function finishMinting() public onlyOwner returns (bool) {
        mintingFinished = true;
        emit MintFinished();
        return true;
    }
}


contract SimpleMasterTokenCoin is MintableMasterToken {
    bytes32 public name;
    bytes32 public symbol;
    uint32 public decimals;
    function getName() public /*constant*/ view returns (bytes32) {
        return name;
    }
    function getSymbol() public /*constant*/ view returns (bytes32) {
        return symbol;
    }
    function getDecimals() public /*constant*/ view returns (uint32) {
        return decimals;
    }
    constructor (bytes32 tokenName, bytes32 symbolToken, uint32 decimalsToken) public {
        name = tokenName;
        symbol = symbolToken;
        decimals = decimalsToken;
    }
    
    // //HYH
    function setDecimals(uint32 decimals_) public /*constant*/ returns (bool) {
        decimals = decimals_;
        
        return true;
    }
}


contract MasterDBOT is Ownable {
    using SafeMath for uint256;
    bytes32 public version;
    SimpleMasterTokenCoin private tmpSymbolToken;
    
    //SimpleMasterTokenCoin private tmpSymbolToken1;  //HYH

    bytes32[] tmpBytes32Array;
    uint256[] tmpUint256Array;

    event Mint(address indexed to, bytes32 name, uint256 amount);
    event Burn(address indexed to, bytes32 name, uint256 amount);
    event TransferApprove(
        address indexed from,
        address indexed to,
        bytes32 exchangeRef,
        bytes32 from_name,
        uint256 from_value,
        bytes32 to_name,
        uint256 to_value,
        bytes32 pay_name,
        uint256 to_payForTransactionValue
        );
    struct SimpleTokenStruct {
        SimpleMasterTokenCoin contractAddress;
        bytes32 name;
        bytes32 symbol;
        uint32 decimals;
    }
    SimpleTokenStruct[] private tokens;
    //////

    SimpleMasterTokenCoin private tmpDealDealerToken;
    SimpleMasterTokenCoin private tmpDealParticipantToken;
    SimpleMasterTokenCoin private tmpLiveDealerToken;
    SimpleMasterTokenCoin private tmpLiveParticipantToken;
    SimpleMasterTokenCoin private tmpCoinToken;
    SimpleMasterTokenCoin private tmpLiveCoinToken;


    event DealCreate(
        bytes32 dealerTokenName,
        address dealerWallet,
        uint256 dealerFundsAmount,
        uint256 dealerMinFundsAmount,
        //bytes32 participantTokenName,
        //uint256 minInvestmentFunds,
        uint256 ts_start,
        uint256 ts_end,
        //uint256 min_funds,
        //uint256 max_funds,
        bytes32 payTokenName,
        uint256 payDealerFunds,
        uint256 payParticipantFunds,
        bool finalizedState
    );


    struct SimpleParticipateTokens {
        bytes32 tokenName;
        SimpleMasterTokenCoin dealDealerTokenAddress;
        SimpleMasterTokenCoin dealParticipantTokenAddress;
        SimpleMasterTokenCoin liveParticipantTokenAddress;
    }

    struct SimpleDealStruct {
        bytes32 dealName;
        bytes32 dealerTokenName;
        address dealerWallet;
        uint256 dealerFundsAmount;
        uint256 dealerMinFundsAmount;
        SimpleMasterTokenCoin dealDealerTokenAddress;
        SimpleMasterTokenCoin liveDealerTokenAddress;
        bytes32[] participantTokensName;
        bool finalizedState;
        mapping (uint256 => SimpleParticipateTokens) dealParticipantTokens;
    }

    uint256 private numDeals;
    mapping (uint256 => SimpleDealStruct) private deals;

    struct SimpleDealStructExtend {
        bytes32 dealName;
        SimpleMasterTokenCoin coinTokenAddress;
        SimpleMasterTokenCoin coinLiveTokenAddress;
        //uint256 minInvestmentFunds;
        uint256 ts_start;
        uint256 ts_end;
        //uint256 min_funds;
        //uint256 max_funds;
        bytes32 payTokenName;
        uint256 payDealerFunds;
        uint256 payParticipantFunds;
    }

    mapping (uint256 => SimpleDealStructExtend) private dealsExtend;

    constructor() public {
        version = '0.14.2';
        tmpSymbolToken = new SimpleMasterTokenCoin('system','system',0);
        tokens.push(SimpleTokenStruct({
            contractAddress: tmpSymbolToken,
            name: 'system',
            decimals: 0,
            symbol: 'system'
        }));
        numDeals = 0;
    }
    function getContractVersion() external onlyOwner /*constant*/ view returns (bytes32) {
        return version;
    }
    
    //HYH
    function getTokenSymbolList() external /*constant*/ view returns (bytes32[] memory _ret, uint32[] memory decimals) {
        bytes32[]    memory sl = new bytes32[](tokens.length);
        uint32[]    memory sd = new uint32[](tokens.length);
        for (uint256 i = 0; i<tokens.length; i++) {
            SimpleTokenStruct memory st = tokens[i];
            sl[i] = st.name;
            sd[i] = st.decimals;
        }
        return (sl, sd);
    }
    
    
    //HYH
    //event Print(bytes32, uint32, uint32);
    function createToken(bytes32 tokenName, bytes32 tokenSymbol, uint32 decimalUnits) external onlyOwner returns (bool success) {


        for (uint i = 0; i<tokens.length; i++) {
            //emit Print(tokens[i].name, tokens[i].decimals, decimalUnits);
                        
            require(tokens[i].name != tokenName);
        }
        tmpSymbolToken = new SimpleMasterTokenCoin(tokenName,tokenSymbol,decimalUnits);
        tokens.push(SimpleTokenStruct({
            contractAddress: tmpSymbolToken,
            name: tokenName,
            decimals: decimalUnits,
            symbol: tokenSymbol
        }));

        return true;
    }
    
    //event Print(uint32, uint256, address);
    function setTokenFractionbase(bytes32 tokenName, uint32 fractionBase, address[] calldata _wallets) external onlyOwner returns (bool success){
        uint32 i = 0;
        for (; i < tokens.length; i++){
            //emit Print(tokens[i].name, tokens[i].decimals); 
            
            if (tokens[i].name == tokenName){
                
                uint256 dif = fractionBase - tokens[i].decimals;
                
                 if (dif > 0)
                 {
                    //tokens[i].decimals = fractionBase;
                    tokens[i].contractAddress.setDecimals(fractionBase);
                     for (uint32 j = 1; j <= (uint256)(_wallets[0]); j++){
                        //emit Print(fractionBase, (uint256)(_wallets[0]), _wallets[j]);
                        //callReturnP(address(tokens[i].contractAddress), "powOf(address,int)", _wallets[j], dif);
                         //address(tokens[i].contractAddress).call(bytes4(keccak256("powOf(address,uint256)")),_wallets[j], dif);
                        //address(tokens[i].contractAddress).call(bytes4(keccak256("mint(address,uint256)")),_wallets[j],5);

                        tokens[i].contractAddress.powOf(_wallets[j], dif);
                    }
                    return true;
                }else{
                    require(true == false);
                }    
            }
        }
        //emit Print(tokenName, i);        
        
        if (i >= tokens.length)
             require(true == false);
            
        return false;
    }

    
    
    
    
    //HYH
    function mint(bytes32 name, address _to, uint256 _amount) external onlyOwner returns (bool) {
        
        for (uint32 i = 0; i<tokens.length; i++) {
            //emit Print(tokens[i].name, name, i);
            
            if (tokens[i].name == name) {
 
 
 
                // tmpSymbolToken1 = tokens[i].contractAddress;  //tokens[i].contractAddress.mint(_to, _amount);
                // if (tmpSymbolToken1 == address(0))
                //     emit Print(tokens[i].name, name, i);
                // else
                // {
                //     emit Print("name", "name", 1);
                //     //tmpSymbolToken1.mint1(_to, _amount);
                //     address(tmpSymbolToken1).call(bytes4(keccak256("mint1(address,uint256)")),_to,11);
                // }
                
        //address(tokens[i].contractAddress).call(bytes4(keccak256("mint(address,uint256)")),_to,_amount);
                tokens[i].contractAddress.mint(_to, _amount);
                
                
                emit Mint(_to, tokens[i].name, _amount);
                return true;
            }
        }

        
        require(true==false);
    }
    
    
    
    
    //HYH
    function burn(bytes32 name, address _to, uint256 _amount) external onlyOwner returns (bool) {
        for (uint i = 0; i<tokens.length; i++) {
            if (tokens[i].name == name) {
                tokens[i].contractAddress.burn(_to, _amount);
                //address(tokens[i].contractAddress).call(bytes4(keccak256("burn(address,uint256)")),_to,_amount);
                
                
                emit Burn(_to, tokens[i].name, _amount);
                return true;
            }
        }
        require(true==false);
    }
    
    
    
    
    function freezeMint(bytes32 name, address _to, uint256 _amount) external onlyOwner returns (bool) {
        for (uint i = 0; i<tokens.length; i++) {
            if (tokens[i].name == name) {
                tokens[i].contractAddress.freezeMint(_to, _amount);
                return true;
            }
        }
        require(true==false);
    }

    function freezeBurn(bytes32 name, address _to, uint256 _amount) external onlyOwner returns (bool) {
        for (uint i = 0; i<tokens.length; i++) {
            if (tokens[i].name == name) {
                tokens[i].contractAddress.freezeBurn(_to, _amount);
                return true;
            }
        }
        require(true==false);
    }
    
    //HYH
    //event Print(bytes32, bytes32);
    function balanceOf(bytes32 name, address _owner) external /*constant*/ view returns (uint256) {
        for (uint i = 0; i<tokens.length; i++) {
            //emit Print(tokens[i].name, name);
            if (tokens[i].name == name) {
                return tokens[i].contractAddress.balanceOf(_owner);
                //return (uint256)(address(tokens[i].contractAddress).call(bytes4(keccak256("balanceOf(address)")), _owner));
                
            //return callReturn(address(tokens[i].contractAddress), "balanceOf(address)", _owner);
            }
        }
        require(true==false);
    }
    // function get(address _addr, string _func) public view returns (bytes data) {
    //     uint len = 32;
    //     uint ptr;
    
    //     bytes4 sig = bytes4(keccak256(_func));
    //     assembly {
    //         ptr := mload(0x40)       
    //         mstore(ptr, sig)
    
    //         let result := call(5000, _addr, 0, ptr, 0x4, ptr, add(len, 0x40))
    
    //         if eq(result, 0) {
    //             revert(0, 0)
    //         }
    
    //         ptr := add(ptr, 0x40)
    //         mstore(0x40, add(ptr, add(len, 0x40)))
    //     }
    
    //     data = toBytes(ptr, len); 
    // }
    
    // function toBytes(uint _addr, uint _len) internal pure returns (bytes memory bts) {
    //     bts = new bytes(_len);
    //     uint btsptr;
    //     assembly {
    //         btsptr := add(bts, 0x20)
    //     }
    //     copy(_addr, btsptr, _len);
    // }
    
    // function callReturn(address _addr, string memory _func, address _val) public returns (uint answer) {
    //     bytes4 sig = bytes4(keccak256(abi.encodePacked(_func)));
    //     assembly {
    //         // move pointer to free memory spot
    //         let ptr := mload(0x40)
    //         // put function sig at memory spot
    //         mstore(ptr,sig)
    //         // append argument after function sig
    //         mstore(add(ptr,0x04), _val)

    //         let result := call(
    //           5000000, // gas limit
    //           _addr, //sload(dc_slot),  // to addr. append var to _slot to access storage variable
    //           0, // not transfer any ether
    //           ptr, // Inputs are stored at location ptr
    //           0x24, // Inputs are 36 bytes long
    //           ptr,  //Store output over input
    //           0x20) //Outputs are 32 bytes long
            
    //         if eq(result, 0) {
    //             revert(0, 0)
    //         }
            
    //         answer := mload(ptr) // Assign output to answer var
    //         mstore(0x40,add(ptr,0x24)) // Set storage pointer to new space
    //     }
    // }    
    
    //  function callReturnBytes32(address _addr, string memory _func) public returns (bytes32 answer) {
    //     bytes4 sig = bytes4(keccak256(abi.encodePacked(_func)));
    //     assembly {
    //         // move pointer to free memory spot
    //         let ptr := mload(0x40)
    //         // put function sig at memory spot
    //         mstore(ptr,sig)
    //         // append argument after function sig
    //         //mstore(add(ptr,0x04), _val)

    //         let result := call(
    //           5000000, // gas limit
    //           _addr, //sload(dc_slot),  // to addr. append var to _slot to access storage variable
    //           0, // not transfer any ether
    //           ptr, // Inputs are stored at location ptr
    //           0x24, // Inputs are 36 bytes long
    //           ptr,  //Store output over input
    //           0x20) //Outputs are 32 bytes long
            
    //         if eq(result, 0) {
    //             revert(0, 0)
    //         }
            
    //         answer := mload(ptr) // Assign output to answer var
    //         mstore(0x40,add(ptr,0x24)) // Set storage pointer to new space
    //     }
    // }    
    
    // event Print(address,int);
    // function callReturnP(address _addr, string _func, address _val, int p) public returns (uint answer) {
    //     emit Print(_val, p);
        
    //     bytes4 sig = bytes4(keccak256(abi.encodePacked(_func)));
    //     assembly {
    //         // move pointer to free memory spot
    //         let ptr := mload(0x40)
    //         // put function sig at memory spot
    //         mstore(ptr,sig)
    //         // append argument after function sig
    //         mstore(add(ptr,0x04), _val)
    //         mstore(add(ptr,0x24), p)
            
    //         let result := call(
    //           5000000, // gas limit
    //           _addr, //sload(dc_slot),  // to addr. append var to _slot to access storage variable
    //           0, // not transfer any ether
    //           ptr, // Inputs are stored at location ptr
    //           0x24, // Inputs are 36 bytes long
    //           ptr,  //Store output over input
    //           0x20) //Outputs are 32 bytes long
            
    //         if eq(result, 0) {
    //             revert(0, 0)
    //         }
            
    //         answer := mload(ptr) // Assign output to answer var
    //         mstore(0x40,add(ptr,0x24)) // Set storage pointer to new space
    //     }
    // }
    
    
    
    
    
    
    function totalSupply(bytes32 name) external /*constant*/ view returns (uint256 _balance) {
        for (uint i = 0; i<tokens.length; i++) {
            if (tokens[i].name == name) {
                return tokens[i].contractAddress.getTotalSupply();
            }
        }
        require(true==false);
    }




    //HYH

    function transferApprove(
                                bytes32 name_symbol,
                                bytes32 name_trade,
                                bytes32 name_consume,
                                bytes32 exchangeRef,
                                address _buyside,
                                address _sellside,
                                uint256 _cash_value,
                                uint256 _fund_value,
                                uint256 _pay_value
                            ) external onlyOwner returns (bool success) {

        uint256 i_symbol = 0;
        uint256 i_trade = 0;
        uint256 i_consume = 0;
        for (uint i = 0; i<tokens.length; i++) {
            if (tokens[i].name == name_symbol) {
                i_symbol = i;
            }
            if (tokens[i].name == name_trade) {
                i_trade = i;
            }
            if (tokens[i].name == name_consume) {
                i_consume = i;
            }
        }
        
        require(i_symbol != 0);
        require(i_trade != 0);
        require(i_consume != 0);

        
        require(i_symbol != i_trade);
        require(i_symbol != i_consume);
        require(i_trade != i_consume);
        
        // require(callReturn(address(tokens[i_trade].contractAddress), "balanceOf(address)", _buyside) >= _cash_value);
        // require(callReturn(address(tokens[i_symbol].contractAddress), "balanceOf(address)", _sellside) >= _fund_value);

        
        //address co = currentOwner();
        

        
        
        if (_buyside == _sellside) {
             tokens[i_consume].contractAddress.approve(_buyside, currentOwner(), _pay_value);
             tokens[i_consume].contractAddress.transferFrom(_buyside, currentOwner(), _pay_value);
            
             tokens[i_consume].contractAddress.approve(_sellside, currentOwner(), _pay_value);
             tokens[i_consume].contractAddress.transferFrom(_sellside, currentOwner(), _pay_value);
            

            // address(tokens[i_consume].contractAddress).call(bytes4(keccak256("approve(address,address,uint256)")),_buyside, co, _pay_value);
            // address(tokens[i_consume].contractAddress).call(bytes4(keccak256("transferFrom(address,address,uint256)")),_buyside, co, _pay_value);
            // address(tokens[i_consume].contractAddress).call(bytes4(keccak256("approve(address,address,uint256)")),_sellside, co, _pay_value);
            // address(tokens[i_consume].contractAddress).call(bytes4(keccak256("transferFrom(address,address,uint256)")),_sellside, co, _pay_value);
        } else {
            tokens[i_trade].contractAddress.approve(_buyside, _sellside, _cash_value);
            tokens[i_symbol].contractAddress.approve(_sellside, _buyside, _fund_value);
            tokens[i_consume].contractAddress.approve(_buyside, currentOwner(), _pay_value);
            tokens[i_consume].contractAddress.approve(_sellside, currentOwner(), _pay_value);
            
            tokens[i_trade].contractAddress.transferFrom(_buyside, _sellside, _cash_value);
            tokens[i_symbol].contractAddress.transferFrom(_sellside, _buyside, _fund_value);
            tokens[i_consume].contractAddress.transferFrom(_buyside, currentOwner(), _pay_value);
            tokens[i_consume].contractAddress.transferFrom(_sellside, currentOwner(), _pay_value);
            
            //bool rt;
        //address(tokens[i_trade].contractAddress).call(bytes4(keccak256("approve(address,address,uint256)")),_buyside, _sellside, _cash_value);
            // if (rt == true)
            //     Print(100);
            // else
            //     Print(101);
        //address(tokens[i_symbol].contractAddress).call(bytes4(keccak256("approve(address,address,uint256)")),_sellside, _buyside, _fund_value);
            // if (rt == true)
            //     Print(200);
            // else
            //     Print(201);
        //address(tokens[i_consume].contractAddress).call(bytes4(keccak256("approve(address,address,uint256)")),_buyside, co, _pay_value);
            // if (rt == true)
            //     Print(300);
            // else
            //     Print(301);
        //address(tokens[i_consume].contractAddress).call(bytes4(keccak256("approve(address,address,uint256)")),_sellside, co, _pay_value);
            // if (rt == true)
            //     Print(400);
            // else
            //     Print(401);
            
            
            
        //address(tokens[i_trade].contractAddress).call(bytes4(keccak256("transferFrom(address,address,uint256)")),_buyside, _sellside, _cash_value);
            // if (rt == true)
            //     Print(500);
            // else
            //     Print(501);
        //address(tokens[i_symbol].contractAddress).call(bytes4(keccak256("transferFrom(address,address,uint256)")),_sellside, _buyside, _fund_value);
            // if (rt == true)
            //     Print(600);
            // else
            //     Print(601);
        //address(tokens[i_consume].contractAddress).call(bytes4(keccak256("transferFrom(address,address,uint256)")),_buyside, co, _pay_value);
            // if (rt == true)
            //     Print(700);
            // else
            //     Print(701);
        //address(tokens[i_consume].contractAddress).call(bytes4(keccak256("transferFrom(address,address,uint256)")),_sellside, co, _pay_value);
            // if (rt == true)
            //     Print(800);
            // else
            //     Print(801);
        }
        // emit TransferApprove(
        //                 _buyside,
        //                 _sellside,
        //                 exchangeRef,
        //                 callReturnBytes32(address(tokens[i_trade].contractAddress), "getName()"), //HYH tokens[i_trade].contractAddress.getName(),
        //                 _cash_value,
        //                 callReturnBytes32(address(tokens[i_symbol].contractAddress), "getName()"), //HYH tokens[i_symbol].contractAddress.getName(),
        //                 _fund_value,
        //                 callReturnBytes32(address(tokens[i_consume].contractAddress), "getName()"), //HYH tokens[i_consume].contractAddress.getName(),
        //                 _pay_value
        //             ); 

        emit TransferApprove(
                        _buyside,
                        _sellside,
                        exchangeRef,
                        tokens[i_trade].contractAddress.getName(),
                        _cash_value,
                        tokens[i_symbol].contractAddress.getName(),
                        _fund_value,
                        tokens[i_consume].contractAddress.getName(),
                        _pay_value
                    ); 
        return true;
    }
    
    
    
    
    
    
    
    function balanceByToken(bytes32 token_name, address[] calldata wallets) external /*constant*/ view returns (uint256[] memory _ret) {
        for (uint256 i = 0; i<tokens.length; i++) {
            if (tokens[i].name == token_name) {
                uint256[]    memory funds = new uint256[](wallets.length);
                for (uint256 j =0; j<wallets.length; j++) {
                    funds[j] = tokens[i].contractAddress.balanceOf(wallets[j]);
                }
                return funds;
            }
        }
        require(true == false);
    }
    function balanceFrozenByToken(bytes32 token_name, address[] calldata wallets) external /*constant*/ view returns (uint256[] memory _ret) {
        for (uint256 i = 0; i<tokens.length; i++) {
            if (tokens[i].name == token_name) {
                uint256[]    memory funds = new uint256[](wallets.length);
                for (uint256 j =0; j<wallets.length; j++) {
                    funds[j] = tokens[i].contractAddress.freezeBalanceOf(wallets[j]);
                }

                return funds;
            }
        }
        require(true == false);
    }
    
    
    
    
    
   // event Print(bytes32);
    function balanceByWallet(address wallet, bytes32[] calldata income_tokens) external /*constant*/ view returns (uint256[] memory _ret) {
        uint256[]    memory funds = new uint256[](income_tokens.length);
        for (uint256 j = 0; j<income_tokens.length; j++) {
            funds[j] = 0;
            for (uint256 i = 0; i<tokens.length; i++) {
                //emit Print(income_tokens[j]);
                if (income_tokens[j] == tokens[i].name) {
                    funds[j] = tokens[i].contractAddress.balanceOf(wallet);
                //funds[j] = callReturn(address(tokens[i].contractAddress), "balanceOf(address)", wallet);
                }
            }
        }
        return funds;
    }
    function balanceFrozenByWallet(address wallet, bytes32[] calldata income_tokens) external /*constant*/ view returns (uint256[] memory _ret) {
        uint256[]    memory funds = new uint256[](income_tokens.length);
        for (uint256 j = 0; j<income_tokens.length; j++) {
            funds[j] = 0;
            for (uint256 i = 0; i<tokens.length; i++) {
                if (income_tokens[j] == tokens[i].name) {
                    funds[j] = tokens[i].contractAddress.freezeBalanceOf(wallet);
                //funds[j] = callReturn(address(tokens[i].contractAddress), "freezeBalanceOf(address)", wallet);
                }
            }
        }
        return funds;
    }
    
    
    
    
    
    
    
    function totalSupplyByTokens(bytes32[] calldata income_tokens) external /*constant*/ view returns (uint256[] memory _ret) {
        uint256[]    memory funds = new uint256[](income_tokens.length);
        for (uint256 j = 0; j<income_tokens.length; j++) {
            funds[j] = 0;
            for (uint256 i = 0; i<tokens.length; i++) {
                if (income_tokens[j] == tokens[i].name) {
                    funds[j] = tokens[i].contractAddress.getTotalSupply();
                }
            }
        }
        return funds;
    }
    function getTokenAddress(bytes32 name) external /*constant*/ view returns (SimpleMasterTokenCoin) {
        for (uint256 i = 0; i<tokens.length; i++) {
            if (name == tokens[i].name) {
                return tokens[i].contractAddress;
            }
        }
        require(true == false);
    }
    function getTokenSymbol(bytes32 name) external /*constant*/ view returns (bytes32) {
        for (uint256 i = 0; i<tokens.length; i++) {
            if (name == tokens[i].name) {
                return tokens[i].symbol;
            }
        }
        require(true == false);
    }
    function getTokenDecimals(bytes32 name) external /*constant*/ view returns (uint32) {
        for (uint256 i = 0; i<tokens.length; i++) {
            if (name == tokens[i].name) {
                return tokens[i].decimals;
            }
        }

        require(true == false);
    }


    function dealCreate(
        bytes32 dealName,
        bytes32 dealerTokenName,
        address dealerWallet,
        uint256 dealerFundsAmount,
        uint256 dealerMinFundsAmount,
        uint256 ts_start,
        uint256 ts_end,
        bytes32[] calldata participantTokensName,
        bytes32 payTokenName,
        uint256 payDealerFunds,
        uint256 payParticipantFunds

    ) external onlyOwner returns (bool) {
        // create structure
        uint256 index = dealCreateStep1(dealName);

        /// ----

        dealCreateStep2(
            index,
            dealerTokenName,
            dealerWallet,
            dealerFundsAmount,
            dealerMinFundsAmount,
            ts_start,
            ts_end,
            participantTokensName,
            payTokenName,
            payDealerFunds,
            payParticipantFunds
        );

        /// ----
        dealCreateStep3(index);

        // event creation
//        //dealCreateStep4(index);

        return true;
        //return index;

    }

    function dealCreateStep1(bytes32 dealName) private onlyOwner returns (uint256) {
        uint256 i = 0;
        uint256 index = 0;

        // Check deal exist
        for (i = 0; i<numDeals; i++) {
            require(deals[i].dealName != dealName);
        }

        index = numDeals;
        numDeals++;

        deals[index] = SimpleDealStruct(
            dealName,     //dealName:
            'dummy',    //dealerTokenName:
            address(0), //dealerWallet:
            0, //dealerFundsAmount:
            0, //dealerMinFundsAmount:
            tmpSymbolToken, //dealDealerTokenAddress:
            tmpSymbolToken, //liveDealerTokenAddress:
            // skipped //dealParticipantTokens:
            tmpBytes32Array,
            false   //finalizedState:
        );

        dealsExtend[index] = SimpleDealStructExtend({
            dealName: dealName,
            coinTokenAddress: tmpSymbolToken,
            coinLiveTokenAddress: tmpSymbolToken,
            ts_start: 0,
            ts_end: 0,
            payTokenName:'dummy',
            payDealerFunds:0,
            payParticipantFunds:0
        });

        return index;

    }

    function dealCreateStep2(
        uint256 index,
        bytes32 dealerTokenName,
        address dealerWallet,
        uint256 dealerFundsAmount,
        uint256 dealerMinFundsAmount,
        uint256 ts_start,
        uint256 ts_end,

        bytes32[] memory participantTokensName,

        bytes32 payTokenName,
        uint256 payDealerFunds,
        uint256 payParticipantFunds
    ) private onlyOwner returns (bool) {

        deals[index].dealerTokenName = dealerTokenName;
        deals[index].dealerWallet = dealerWallet;
        deals[index].dealerFundsAmount = dealerFundsAmount;
        deals[index].dealerMinFundsAmount = dealerMinFundsAmount;

        dealsExtend[index].ts_start = ts_start;
        dealsExtend[index].ts_end = ts_end;
        deals[index].participantTokensName = participantTokensName;


        dealsExtend[index].payTokenName = payTokenName;
        dealsExtend[index].payDealerFunds = payDealerFunds;
        dealsExtend[index].payParticipantFunds = payParticipantFunds;


        return true;
    }


    function dealCreateStep3(uint256 index) private onlyOwner returns (bool) {


        // Dealer Token Create
        tmpDealDealerToken = new SimpleMasterTokenCoin(
                                                        deals[index].dealerTokenName,
                                                        this.getTokenSymbol(deals[index].dealerTokenName),
                                                        this.getTokenDecimals(deals[index].dealerTokenName)
                                                        );

        tmpLiveDealerToken = this.getTokenAddress(deals[index].dealerTokenName);


        // Participant Token Create
        uint256 i = 0;
        for (i = 0; i<deals[index].participantTokensName.length; i++) {
            deals[index].dealParticipantTokens[i] = SimpleParticipateTokens(
                deals[index].participantTokensName[i],
                new SimpleMasterTokenCoin(
                     deals[index].dealerTokenName,
                     this.getTokenSymbol(deals[index].dealerTokenName),
                     this.getTokenDecimals(deals[index].dealerTokenName)
                ),
                new SimpleMasterTokenCoin(
                     deals[index].participantTokensName[i],
                     this.getTokenSymbol(deals[index].participantTokensName[i]),
                     this.getTokenDecimals(deals[index].participantTokensName[i])
                ),
                this.getTokenAddress(deals[index].participantTokensName[i])
            );

        }
        //////////////////////////////////////

        // Payable Token create
        tmpCoinToken = new SimpleMasterTokenCoin(
                                                        dealsExtend[index].payTokenName,
                                                        this.getTokenSymbol(dealsExtend[index].payTokenName),
                                                        this.getTokenDecimals(dealsExtend[index].payTokenName)
        );
        tmpLiveCoinToken = this.getTokenAddress(dealsExtend[index].payTokenName);

        //// Checking Correct
        // require(tmpDealDealerToken != address(0));
        // require(tmpLiveDealerToken != address(0));
        // for (i = 0; i<deals[index].participantTokensName.length; i++) {
        //     require(deals[index].dealParticipantTokens[i].dealDealerTokenAddress != address(0));
        //     require(deals[index].dealParticipantTokens[i].dealParticipantTokenAddress != address(0));
        //     require(deals[index].dealParticipantTokens[i].liveParticipantTokenAddress != address(0));
        // }

        // require(tmpCoinToken != address(0));
        // require(tmpLiveCoinToken != address(0));

        // Make Freeze Coin(payDealerCoinValue) for Dealer
        tmpLiveCoinToken.freeze(deals[index].dealerWallet, dealsExtend[index].payDealerFunds);
        tmpCoinToken.mint(deals[index].dealerWallet, dealsExtend[index].payDealerFunds);

        // filling token object's to structure
        deals[index].dealDealerTokenAddress = tmpDealDealerToken;
        deals[index].liveDealerTokenAddress = tmpLiveDealerToken;

        dealsExtend[index].coinTokenAddress = tmpCoinToken;
        dealsExtend[index].coinLiveTokenAddress = tmpLiveCoinToken;

        return true;

}

//    function dealCreateStep4(uint256 index) private onlyOwner returns (bool) {
//        DealCreate(
//            deals[index].dealerTokenName,
//            deals[index].dealerWallet,
//            deals[index].dealerFundsAmount,
//            //deals[index].participantTokenName,
//            //dealsExtend[index].minInvestmentFunds,
//            dealsExtend[index].ts_start,
//            dealsExtend[index].ts_end,
//            //dealsExtend[index].min_funds,
//            //dealsExtend[index].max_funds,
//            dealsExtend[index].payTokenName,
//            dealsExtend[index].payDealerFunds,
//            dealsExtend[index].payParticipantFunds,
//            deals[index].finalizedState
//        );
//        return true;
//    }


    function dealMake(
        bytes32 dealName,
        address participantWallet,
        bytes32 participantToken,
        uint256 participantFunds,
        uint256 dealerFunds
    ) external onlyOwner returns (bool) {
uint256 i = 0;
uint256 j = 0;

        for (i = 0; i<numDeals; i++) {
            if (deals[i].dealName == dealName) {


                uint256 max_funds = deals[i].dealerFundsAmount;


                uint256 total = deals[i].dealDealerTokenAddress.getTotalSupply();

                if (dealsExtend[i].ts_start > block.timestamp) {
                    // not start yet
                    require(true==false);



                } else if (block.timestamp < dealsExtend[i].ts_end && total < max_funds ) {


                    for (j = 0; j<deals[i].participantTokensName.length; j++) {
                        if (deals[i].dealParticipantTokens[j].tokenName == participantToken) {


                            uint fundsToMint = 0;
                            uint fundsToDealer = 0;

                            if (total.add(dealerFunds) >  max_funds) {

                                // get diff(available), mint and finalize
                                fundsToDealer = max_funds.sub(total);
                                fundsToMint = fundsToDealer.mul(participantFunds).div(dealerFunds);

                            } else {
                                // all ok just mint
                                fundsToDealer = dealerFunds;
                                fundsToMint = participantFunds;
                            }


                            dealMake2(i,j,participantWallet,fundsToMint, fundsToDealer);
                            return true;

                        }
                    }



                }

            }
        }
        // exception
        require(true==false);
    }

    function dealMake2(uint256 i, uint256 j, address participantWallet,uint256 fundsToMint,uint256 fundsToDealer) private onlyOwner returns (bool) {


        // make freeze coins

        dealsExtend[i].coinLiveTokenAddress.freeze(participantWallet, dealsExtend[i].payParticipantFunds);
        dealsExtend[i].coinTokenAddress.mint(participantWallet, dealsExtend[i].payParticipantFunds);

        // make freeze and mint
        deals[i].dealParticipantTokens[j].liveParticipantTokenAddress.freeze(participantWallet, fundsToMint);
        deals[i].dealParticipantTokens[j].dealParticipantTokenAddress.mint(participantWallet, fundsToMint);

        // additional for dealer calculation
        deals[i].dealDealerTokenAddress.mint(deals[i].dealerWallet, fundsToDealer);
        deals[i].dealParticipantTokens[j].dealDealerTokenAddress.mint(participantWallet, fundsToDealer);

        return true;
    }

    function dealPayback(bytes32 dealName,address[] calldata wallets) external onlyOwner returns (bool) {
        return payback(dealName,wallets);
    }

    function  payback(bytes32 dealName,address[] memory wallets) private onlyOwner returns (bool) {
        uint i = 0;
        uint j = 0;
        uint x = 0;
        uint funds = 0;
        for (i = 0; i<numDeals; i++) {
            if (deals[i].dealName == dealName) {
                // make Payback //
                for (j =0; j<wallets.length; j++) {
                    // make unfreeze coins
                    funds =  dealsExtend[i].coinTokenAddress.balanceOf(wallets[j]);
                    dealsExtend[i].coinLiveTokenAddress.unFreeze(wallets[j], funds);

                    // make unfreeze
                    for (x = 0; x<deals[i].participantTokensName.length; x++) {
                        funds =  deals[i].dealParticipantTokens[x].dealParticipantTokenAddress.balanceOf(wallets[j]);
                        deals[i].dealParticipantTokens[x].liveParticipantTokenAddress.unFreeze(wallets[j], funds);
                    }
                }
            }
        }
    }


    function dealFinalize(
        bytes32 dealName,
        address[] calldata wallets
    ) external onlyOwner returns (bool) {

        for (uint i = 0; i<numDeals; i++) {
            if (deals[i].dealName == dealName) {
                if (deals[i].finalizedState == false) {

                    uint256 total = deals[i].dealDealerTokenAddress.getTotalSupply();

                    uint256 totalDealFunds = deals[i].dealerFundsAmount;

                    if (total >= totalDealFunds) {
                        // make ICO //

                        dealFinalizeICO(i, wallets);


                    } else {

                        payback(dealName,wallets);

                    }

                    deals[i].finalizedState = true;

                }
            }
        }

    }

    function dealFinalizeICO(  uint256 i,address[] memory wallets) private onlyOwner returns (bool) {

        uint x = 0;
        uint funds = 0;

        // mint for dealer
        for (x = 0; x<deals[i].participantTokensName.length; x++) {
            deals[i].dealParticipantTokens[x].liveParticipantTokenAddress.mint(
                deals[i].dealerWallet,
                deals[i].dealParticipantTokens[x].dealParticipantTokenAddress.getTotalSupply()
            );
        }
        ////

        // coin consuming
        funds =  dealsExtend[i].coinTokenAddress.balanceOf(deals[i].dealerWallet);
        dealsExtend[i].coinLiveTokenAddress.freezeBurn(deals[i].dealerWallet, funds);
        dealsExtend[i].coinLiveTokenAddress.mint(currentOwner(),funds);

        // mint participant
        for (uint j =0; j<wallets.length; j++) {
            // // burn from freeze
            // funds =  deals[i].dealParticipantTokenAddress.balanceOf(wallets[j]);
            // deals[i].liveParticipantTokenAddress.freezeBurn(wallets[j], funds);
            // // calculate part funds and mint
            // funds = (funds.mul(totalDealFunds)).div(totalParticipantFunds);
            // deals[i].liveDealerTokenAddress.mint(wallets[j], funds);

            // // coin consuming
            // funds =  dealsExtend[i].coinTokenAddress.balanceOf(wallets[j]);
            // dealsExtend[i].coinLiveTokenAddress.freezeBurn(wallets[j], funds);
            // dealsExtend[i].coinLiveTokenAddress.mint(currentOwner(),funds);

            for (x = 0; x<deals[i].participantTokensName.length; x++) {
                // burn from freeze
                funds =  deals[i].dealParticipantTokens[x].dealParticipantTokenAddress.balanceOf(wallets[j]);
                deals[i].dealParticipantTokens[x].liveParticipantTokenAddress.freezeBurn(wallets[j], funds);

                // mint offer
                funds = deals[i].dealParticipantTokens[x].dealDealerTokenAddress.balanceOf(wallets[j]);
                deals[i].liveDealerTokenAddress.mint(wallets[j], funds);

            }
        }
    }


    function dealDealerTokenTotalSupply(bytes32 dealName) external /*constant*/ view returns (uint256 _balance) {
        for (uint i = 0; i<numDeals; i++) {
            if (deals[i].dealName == dealName) {

                uint256 total = deals[i].dealDealerTokenAddress.getTotalSupply();
                return total;
            }
        }
        require(true==false);
    }

    function dealParticipantTokenTotalSupply(bytes32 dealName,bytes32 participantToken) external /*constant*/ view returns (uint256 _balances) {
        for (uint i = 0; i<numDeals; i++) {
            if (deals[i].dealName == dealName) {

                for (uint x = 0; x<deals[i].participantTokensName.length; x++) {
                    if (deals[i].dealParticipantTokens[x].tokenName == participantToken) {
                        return deals[i].dealParticipantTokens[x].dealParticipantTokenAddress.getTotalSupply();
                    }
                }

            }
        }
        require(true==false);
    }

    function dealFinalizedState(bytes32 dealName) external /*constant*/ view returns (bool _ret) {
        for (uint i = 0; i<numDeals; i++) {
            if (deals[i].dealName == dealName) {
                return deals[i].finalizedState;
            }
        }
        require(true==false);
    }

    // function dealParticipantsBalance(bytes32 dealName,bytes32 participantToken,address[] calldata wallets) external /*constant*/ view returns (
    //                                                                                                                         uint256 dealerTotalBalance,
    //                                                                                                                         uint256 participantTotalBalance,
    //                                                                                                                         uint256[] memory participantParticipant,
    //                                                                                                                         uint256[] memory participantDealer
    //                                                                                                                          ) {
    //     for (uint i = 0; i<numDeals; i++) {
    //         if (deals[i].dealName == dealName) {
    //             for (uint x = 0; x<deals[i].participantTokensName.length; x++) {
    //                 if (deals[i].dealParticipantTokens[x].tokenName == participantToken) {


    //                     uint256 total1 = deals[i].dealDealerTokenAddress.getTotalSupply();
    //                     uint256 total2 = deals[i].dealParticipantTokens[x].dealParticipantTokenAddress.getTotalSupply();

    //                     uint256[]    memory funds = new uint256[](wallets.length);
    //                     uint256[]    memory funds2 = new uint256[](wallets.length);

    //                     for (uint256 j = 0; j<wallets.length; j++) {
    //                         //funds[j] =  deals[i].dealParticipantTokenAddress.balanceOf(wallets[j]);
    //                         funds[j] =  deals[i].dealParticipantTokens[x].dealParticipantTokenAddress.balanceOf(wallets[j]);
    //                         funds2[j] =  deals[i].dealParticipantTokens[x].dealDealerTokenAddress.balanceOf(wallets[j]);
    //                     }

    //                     return (total1, total2, funds, funds2);
    //                 }
    //             }
    //         }
    //     }
    //     require(true==false);
    // }

}
