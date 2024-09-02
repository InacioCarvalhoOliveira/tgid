### 0. Arquitetura projeto

    java 17
    SpringBoot 3.3.3
    SqlServer(no Docker)
    Documentação com Swagger
    Hibernate(para migracao direta do banco "prod")
    H2(para testes)

### 0.1 Lnks

* [HebHook endpoint](https://webhook.site/97a8d9f2-17f1-4412-8dea-1b3a0a2a3f2c)  

### 1. Análise dos Requisitos

Principais Requisitos:

- O sistema deve ter dois tipos de usuários: Empresa e Cliente.`<p>`
- O CPF (para Cliente) e o CNPJ (para Empresa) devem ser validados.`<p>`
- Cada Empresa deve ter ao menos um tipo de taxa que será aplicada nas transações (saque ou depósito).`<p>`
- As Empresas devem possuir saldo que é atualizado com os depósitos e saques realizados pelos Clientes, já descontando as taxas de administração.`<p>`
- Clientes podem realizar depósitos e saques pelas Empresas, respeitando o saldo disponível nas Empresas.

### 2. Modelagem dos Objetos

Principais Objetos:

Usuário (abstract): Classe base para Cliente e Empresa.

Atributos: id, nome.
Cliente: Herda de Usuário.

Atributos: CPF, saldo.
Métodos: validarCPF(), depositar(), sacar().
Empresa: Herda de Usuário.

Atributos: CNPJ, saldo, taxaSistema.
Métodos: validarCNPJ(), atualizarSaldo().
Transação: Representa uma operação financeira (depósito ou saque).

Atributos: tipo, valor, empresa, cliente, data.
Métodos: executar().
Sistema: Controlador principal que gerencia os usuários e transações.

Atributos: listaClientes, listaEmpresas.
Métodos: adicionarCliente(), adicionarEmpresa(), realizarTransacao().

### Regra de negócio : Saque x Depósito

Requisitos para Transações Bancárias

1. Requisitos Gerais
   Taxa de Administração: Cada empresa deve ter uma taxa de administração (ou taxa de sistema) que será aplicada nas transações. A taxa deve ser configurável e é aplicada da seguinte forma:
   Depósito: Não há taxa de administração aplicada. O valor depositado é adicionado integralmente ao saldo da empresa.
   Saque: A taxa de administração é aplicada sobre o valor sacado. O cliente paga a taxa e recebe um valor líquido, que é o valor solicitado menos a taxa.
2. Requisitos de Depósito
   Descrição: Quando um cliente realiza um depósito em uma empresa, o saldo da empresa é aumentado pelo valor do depósito.
   Cálculo: O saldo da empresa é incrementado pelo valor total do depósito (sem dedução de taxas).
   Exemplo: Se um cliente deposita R$100,00 em uma empresa, o saldo da empresa é aumentado em R$100,00.
3. Requisitos de Saque
   Descrição: Quando um cliente realiza um saque de uma empresa, o saldo do cliente é diminuído pelo valor total do saque, incluindo a taxa de administração, e o saldo da empresa é aumentado apenas pelo valor sacado.
   Cálculo: O cliente paga a taxa de administração sobre o valor do saque. O valor efetivamente recebido pelo cliente é o valor sacado menos a taxa de administração.

Exemplo: Se um cliente deseja sacar R$100,00 e a taxa de administração é 5%:
O valor total a ser debitado da conta do cliente é R$105,00 (R$100,00 + 5% de R$100,00).
O cliente receberá R$95,00 (R$100,00 - 5% de R$100,00).
O saldo da empresa é aumentado em R$100,00 (o valor sacado).

4. Requisitos de Saldo Insuficiente
   Descrição: Se o cliente tentar realizar um saque e não tiver saldo suficiente para cobrir o valor do saque mais a taxa de administração, a transação deve ser rejeitada.
   Resposta: A API deve retornar um código de status HTTP 400 Bad Request com uma mensagem indicando "Saldo insuficiente para o saque."
5. Resposta da API para Transações
   Sucesso: Se a transação for realizada com sucesso, a API deve retornar um código de status HTTP 200 OK com uma mensagem confirmando a transação.
   Falha: Se ocorrer algum erro, como saldo insuficiente ou erro interno, a API deve retornar o código de status apropriado e uma mensagem de erro descritiva.

Exemplo de Requisitos
Requisito 1: Depósito
ID: REQ-001
Descrição: Quando um cliente deposita um valor, a empresa deve adicionar o valor integralmente ao seu saldo.
Exemplo: Cliente deposita R$100,00. O saldo da empresa aumenta em R$100,00.

Requisito 2: Saque
ID: REQ-002
Descrição: Quando um cliente saca um valor, a empresa deve adicionar o valor sacado ao seu saldo, e o cliente deve pagar a taxa de administração sobre o valor do saque.
Exemplo: Cliente saca R$100,00 com uma taxa de 5%. O cliente paga R$105,00 e recebe R$95,00. O saldo da empresa aumenta em R$100,00.

Requisito 3: Saldo Insuficiente
ID: REQ-003
Descrição: Se o cliente não tiver saldo suficiente para cobrir o valor do saque mais a taxa, a transação deve ser rejeitada e a API deve retornar um erro apropriado.
Resposta: 400 Bad Request com mensagem "Saldo insuficiente para o saque."

### Casos de Testes:

Casos de Teste para TransactionService

1. Teste de Depósito Bem-Sucedido
   Objetivo: Verificar se uma transação de depósito é processada corretamente e se o e-mail de notificação é enviado com as informações corretas.

Preparação:

Criação de um cliente (testClient) com saldo inicial de R$200,00 e um e-mail.
Criação de uma empresa (testCompany) com saldo inicial de R$1000,00 e taxa de sistema de 5%.
Mock das respostas dos serviços ClientService e CompanyService para retornar o cliente e a empresa criados.
Execução:

Chamada do método runTransaction do TransactionService com os seguintes parâmetros:
Tipo: "deposito"
Valor: 100.00
ID da Empresa: 8L
ID do Cliente: 1L
Data: "01/09/2024"
URL de Callback: null
Notificação: "email"
Verificações:

Saldo da Empresa:

Verifica se o saldo da empresa foi atualizado corretamente após o depósito.
O saldo esperado deve ser o saldo inicial da empresa mais o valor do depósito.
Envio de E-mail:

Verifica se o método sendEmail do EmailService foi chamado exatamente uma vez.
Captura o e-mail enviado usando ArgumentCaptor e verifica:
Destinatário do e-mail (getTo()).
Assunto do e-mail (getSubject()).
Corpo do e-mail (getBody()), que deve corresponder ao formato esperado

    Dear [Nome do Cliente],

    Your recent transaction was successful.

    Transaction Details:
        Type: deposito
        Amount: 100.00
        Date: 01/09/2024
        Updated Balance: 1100.00

    Thank you for choosing our services.

    Best regards,
        Your Company

### Testes fixos obejevos da proposta:

![swagger](/doc/img/swagger.png)
#
![Webhook](/doc/img/hebhook.png)
#
![mailtrap](/doc/img/mailtra.png)
#