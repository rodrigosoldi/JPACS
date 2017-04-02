# JPACS
Projeto de Sistemas Tolerantes à falhas

Emannuel Fernandes de Oliveira Carvalho  - 41330056
Rodrigo Soldi Lopes  - 41344391


Para a implementação do controle de paridade uma nova classe intitulada “MemoryObject" foi criada. Essa classe contém a informação (“element”) e também a paridade, que será utilizada para o cálculo. Todas as operações feitas entre o processador e a memória, agora recebem um objeto do tipo “MemoryObject” para que sejam realizadas as operações e cálculos de paridade. 
Uma controller foi criada, “MemoryAccess”, essa classe é responsável por efetuar todos as operações de leitura e gravação, bem como os cálculos de paridade. O processador agora usa esses métodos e dentro deles é feito o cálculo de paridade e o “MemoryObject" é criado para retorno da informação.
Nessa operação o processador calcula o bit de paridade antes de pedir para gravar algo na memória, como explicado anteriormente, esse bit de paridade, fica dentro do “MemoryObject”, e este é enviado. Na memória é recalculado o bit de paridade e comparado com o foi recebido pelo processador. Caso os dois bits sejam idênticos a gravação é executada. O mesmo se aplica para envio da memória para o processador.
Algumas alteração na classe “ClasseMemoria” foram efetuadas para que os métodos: Acessar, GravarDireto e Gravar, recebessem uma instância da nova classe criada.
Foi criado um mecanismo de duas tentativas de leitura e/ou gravação e consequentemente o cálculo do bit de paridade novamente em caso de erro. No primeiro erro, a operação em questão é feita novamente, caso o problema continue uma exceção do tipo, JPACSParityException, é lançada com uma mensagem informando o problema, e dessa forma o JPACS se responsabiliza pelo tratamento do problema.
Quando a exceção é lançada, é fato que as duas tentativas já se suscederam e mesmo assim o erro persistiu, dessa forma quando o JPACS captura a exceção, a variável PC recebe o valor -1, dessa maneira a execução é interrompida.
Para as classes que foram criadas, foi adotado o padrão de métodos em inglês, e Camel case com a primeira letra minúscula, o que se difere das classes que já estavam criadas.
Para as alterações a IDE, Netbeans, foi utilizada. o projeto completo consta no .zip
