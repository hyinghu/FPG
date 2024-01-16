echo var Output= > FPG_MnT01.js
solc\solc --optimize --evm-version homestead --combined-json abi,bin,interface FPG_MnT01.sol >> FPG_MnT01.js
