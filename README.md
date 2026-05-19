# Tamagotchi MVC

Projeto Java com Swing, SQLite e organizacao em MVC.

## Por que pode nao rodar

O projeto depende de caminhos relativos. O banco fica em:

```text
database/tamagotchi.db
```

Esse caminho e resolvido a partir da pasta em que o comando `java` foi executado. Portanto, se voce roda o programa fora da pasta `Tamagotchi`, ele procura ou tenta criar outra pasta `database` no lugar errado. Isso pode gerar erro ao carregar o banco ou mensagem de tabela nao encontrada.

O jeito correto e sempre compilar e executar a partir da pasta `Tamagotchi`.

## Requisitos

- JDK 17 ou superior.
- Driver SQLite JDBC em `lib/sqlite-jdbc.jar`.

Confira:

```powershell
java -version
javac -version
Test-Path .\lib\sqlite-jdbc.jar
Test-Path .\database\schema.sql
```

Os dois comandos `Test-Path` precisam retornar `True`.

## Estrutura

```text
Tamagotchi/
|-- database/
|   |-- schema.sql
|   `-- tamagotchi.db
|-- lib/
|   `-- sqlite-jdbc.jar
|-- out/
`-- src/
    |-- controller/
    |-- dao/
    |-- model/
    `-- view/
```

## Como rodar no Windows PowerShell

Execute estes comandos exatamente nesta ordem, a partir da raiz do repositorio:

```powershell
cd Tamagotchi
java -version
javac -version
Test-Path .\lib\sqlite-jdbc.jar
Test-Path .\database\schema.sql
javac -cp "lib\sqlite-jdbc.jar" -d out -sourcepath src src\model\*.java src\dao\*.java src\controller\*.java src\view\*.java
java -cp "lib\sqlite-jdbc.jar;out" view.MainFrame
```

Os dois comandos `Test-Path` precisam retornar `True`. Se algum retornar `False`, o arquivo indicado esta faltando e precisa ser corrigido antes de compilar.

## Testar primeiro no console

Se quiser testar sem abrir a janela Swing:

```powershell
cd Tamagotchi
javac -cp "lib\sqlite-jdbc.jar" -d out -sourcepath src src\model\*.java src\dao\*.java src\controller\*.java src\view\*.java
Write-Output 0 | java -cp "lib\sqlite-jdbc.jar;out" view.GameView
```

Saida esperada:

```text
Pet: Tama  | Tipo: Ovo
Fome      : ...
Felicidade: ...
Energia   : ...
Encerrando Tamagotchi.
```

## Linux/macOS

Execute estes comandos exatamente nesta ordem, a partir da raiz do repositorio:

```bash
cd Tamagotchi
java -version
javac -version
test -f ./lib/sqlite-jdbc.jar
test -f ./database/schema.sql
javac -cp "lib/sqlite-jdbc.jar" -d out -sourcepath src src/model/*.java src/dao/*.java src/controller/*.java src/view/*.java
java -cp "lib/sqlite-jdbc.jar:out" view.MainFrame
```

## Banco de dados

Na primeira execucao, se `database/tamagotchi.db` nao existir, a aplicacao cria o arquivo usando `database/schema.sql`.

O schema cria:

- `creature_type`
- `evolution`
- `pet`

A tabela principal dos pets e `pet`.

## Erros comuns

### Driver SQLite nao encontrado

Verifique se o arquivo existe:

```powershell
Test-Path .\lib\sqlite-jdbc.jar
```

Se retornar `False`, coloque o driver JDBC do SQLite em:

```text
lib/sqlite-jdbc.jar
```

### Banco ou tabela nao encontrada

Confirme que o comando esta sendo executado dentro de `Tamagotchi`:

```powershell
Get-Location
```

Depois, se quiser recriar o banco local:

```powershell
Remove-Item .\database\tamagotchi.db -ErrorAction SilentlyContinue
Write-Output 0 | java -cp "lib\sqlite-jdbc.jar;out" view.GameView
```

### Programa abre pelo terminal, mas nao pelo atalho/IDE

Configure o diretorio de trabalho da execucao para a pasta `Tamagotchi`. O classpath sozinho nao basta, porque o caminho do banco ainda e relativo ao diretorio de trabalho.

Classe principal da interface:

```text
view.MainFrame
```

Classe principal do console:

```text
view.GameView
```

## Observacoes

- Este projeto nao usa MySQL nem PostgreSQL.
- `pet` nao e nome de banco. E uma tabela dentro do SQLite.
- O arquivo `out/` contem classes compiladas e pode ser recriado com o comando `javac`.
