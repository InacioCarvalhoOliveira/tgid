### 1. Análise dos Requisitos
Principais Requisitos:

- O sistema deve ter dois tipos de usuários: Empresa e Cliente.<p>
- O CPF (para Cliente) e o CNPJ (para Empresa) devem ser validados.<p>
- Cada Empresa deve ter ao menos um tipo de taxa que será aplicada nas transações (saque ou depósito).<p>
- As Empresas devem possuir saldo que é atualizado com os depósitos e saques realizados pelos Clientes, já descontando as taxas de administração.<p>
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
