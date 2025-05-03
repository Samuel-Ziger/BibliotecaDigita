# Sistema de Gerenciamento de Biblioteca

Sistema desenvolvido em Java para gerenciamento de empréstimos de livros em uma biblioteca.

## Requisitos

- Java 11 ou superior
- Maven
- PostgreSQL 12 ou superior

## Configuração do Banco de Dados

1. Crie um banco de dados PostgreSQL chamado `biblioteca`
2. Execute o script SQL em `src/main/resources/schema.sql` para criar as tabelas necessárias
3. Configure as credenciais do banco de dados no arquivo `ConexaoDB.java`:
   - URL: jdbc:postgresql://localhost:5432/biblioteca
   - Usuário: postgres
   - Senha: postgres

## Instalação

1. Clone o repositório
2. Execute o comando Maven para compilar o projeto:
   ```bash
   mvn clean install
   ```

## Execução

Execute o comando Maven para iniciar o sistema:
```bash
mvn exec:java -Dexec.mainClass="com.biblioteca.ui.PrincipalFrame"
```

## Funcionalidades

O sistema possui três abas principais:

1. **Alunos**
   - Cadastro de alunos
   - Atualização de dados
   - Exclusão de registros
   - Listagem de alunos

2. **Livros**
   - Cadastro de livros
   - Atualização de dados
   - Exclusão de registros
   - Listagem de livros

3. **Empréstimos**
   - Registro de empréstimos
   - Atualização de dados
   - Exclusão de registros
   - Geração de relatório de empréstimos

## Interface

A interface gráfica foi desenvolvida usando Java Swing, com um layout intuitivo e fácil de usar. Cada aba possui campos específicos para o tipo de dado que está sendo manipulado e botões para as operações CRUD. 