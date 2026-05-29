# Tamagotchi MVC

Aplicacao Java desktop de Tamagotchi, feita com Swing, SQLite, JDBC e organizacao em MVC.

## Specs

- Linguagem: Java
- Interface: Swing
- Banco: SQLite local
- Driver: `lib/sqlite-jdbc.jar`
- Arquitetura: MVC
- Classe principal da GUI: `view.MainFrame`
- Classe principal do console: `view.GameView`
- JDK recomendado: 17 ou superior

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

## Banco De Dados

O banco padrao fica em:

```text
database/tamagotchi.db
```

Tabelas principais:

- `pet`: guarda os pets, status e imagem.
- `pet.image_data`: coluna `BLOB`; a imagem e salva no banco em bytes, nao como caminho.
- `app_setting`: guarda configuracoes simples.
- `app_setting.active_pet_id`: id do pet ativo.
- `creature_type`: tipos base de criatura.
- `evolution`: regras de evolucao.

O backend tambem aceita um diretorio customizado para o banco:

- Variavel de ambiente: `TAMAGOTCHI_DATABASE_DIR`
- Propriedade JVM: `-Dtamagotchi.database.dir=...`

## Rodar No Windows PowerShell

Entre na pasta do projeto:

```powershell
cd "C:\Users\Usuario\Desktop\java implements\testPOO\Tamagotchi"
```

Verifique os requisitos:

```powershell
java -version
javac -version
Test-Path .\lib\sqlite-jdbc.jar
Test-Path .\database\schema.sql
```

Os dois comandos `Test-Path` devem retornar `True`.

Compile:

```powershell
javac -cp ".\lib\sqlite-jdbc.jar" -d out -sourcepath src src\model\*.java src\dao\*.java src\controller\*.java src\view\*.java
```

Execute a interface grafica:

```powershell
java -cp ".\lib\sqlite-jdbc.jar;out" view.MainFrame
```

Execute a versao console:

```powershell
java -cp ".\lib\sqlite-jdbc.jar;out" view.GameView
```

## Rodar No Windows Com JDK Especifico

Use este formato se `java` do PATH for antigo:

```powershell
$JDK = "C:\Program Files\Android\openjdk\jdk-21.0.8"
cd "C:\Users\Usuario\Desktop\java implements\testPOO\Tamagotchi"
& "$JDK\bin\javac.exe" -cp ".\lib\sqlite-jdbc.jar" -d out -sourcepath src src\model\*.java src\dao\*.java src\controller\*.java src\view\*.java
& "$JDK\bin\java.exe" -cp ".\lib\sqlite-jdbc.jar;out" view.MainFrame
```

## Rodar No Linux Ou macOS

Entre na pasta do projeto:

```bash
cd "/caminho/para/testPOO/Tamagotchi"
```

Verifique os requisitos:

```bash
java -version
javac -version
test -f ./lib/sqlite-jdbc.jar
test -f ./database/schema.sql
```

Compile:

```bash
javac -cp "./lib/sqlite-jdbc.jar" -d out -sourcepath src src/model/*.java src/dao/*.java src/controller/*.java src/view/*.java
```

Execute a interface grafica:

```bash
java -cp "./lib/sqlite-jdbc.jar:out" view.MainFrame
```

Execute a versao console:

```bash
java -cp "./lib/sqlite-jdbc.jar:out" view.GameView
```

## Banco Em Diretorio Customizado

Windows PowerShell:

```powershell
$env:TAMAGOTCHI_DATABASE_DIR = "C:\dados\tamagotchi"
java -cp ".\lib\sqlite-jdbc.jar;out" view.MainFrame
```

Ou:

```powershell
java -Dtamagotchi.database.dir="C:\dados\tamagotchi" -cp ".\lib\sqlite-jdbc.jar;out" view.MainFrame
```

Linux/macOS:

```bash
TAMAGOTCHI_DATABASE_DIR="/tmp/tamagotchi-db" java -cp "./lib/sqlite-jdbc.jar:out" view.MainFrame
```

Ou:

```bash
java -Dtamagotchi.database.dir="/tmp/tamagotchi-db" -cp "./lib/sqlite-jdbc.jar:out" view.MainFrame
```

## Uso Basico

Na tela principal:

- `ALIMENTAR`: reduz fome, aumenta felicidade e reduz energia.
- `BRINCAR`: aumenta felicidade, aumenta fome e reduz energia.
- `DORMIR`: recupera energia.
- `EXERCITAR`: aumenta fome, aumenta felicidade e reduz energia.
- `GERENCIAR PETS`: abre o CRUD de pets.

No CRUD:

- `NOVO`: cria um pet.
- `EDITAR`: altera o pet selecionado.
- `EXCLUIR`: remove o pet selecionado.
- `USAR ESTE`: define o pet selecionado como ativo.

## Troubleshooting

### Erro: class file version 61.0

O projeto foi compilado com Java 17 ou superior, mas esta sendo executado com Java 8.

Use um JDK 17+ para compilar e executar:

```powershell
java -version
javac -version
```

### Erro: Driver SQLite nao encontrado

Confirme se o driver existe:

```powershell
Test-Path .\lib\sqlite-jdbc.jar
```

No Linux/macOS:

```bash
test -f ./lib/sqlite-jdbc.jar
```

### Erro Ao Carregar Banco SQLite

Confirme se existe:

```text
database/schema.sql
database/tamagotchi.db
```

Se quiser recriar o banco local:

```powershell
Remove-Item .\database\tamagotchi.db -ErrorAction SilentlyContinue
java -cp ".\lib\sqlite-jdbc.jar;out" view.GameView
```

No Linux/macOS:

```bash
rm -f ./database/tamagotchi.db
java -cp "./lib/sqlite-jdbc.jar:out" view.GameView
```

## Observacoes Para Futuro EXE

- Empacotar com Java 17+.
- Incluir `sqlite-jdbc.jar`.
- Manter um diretorio gravavel para o SQLite.
- Preferir `TAMAGOTCHI_DATABASE_DIR` ou `-Dtamagotchi.database.dir` no atalho/launcher.
- Nao depender do Java 8 instalado na maquina.
