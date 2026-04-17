## Tamagotchi MVC (Java + SQLite + Swing)

Implementação de um **Tamagotchi de verdade** (bichinho virtual), seguindo **MVC**, com:

- Status persistido em **SQLite**.
- Interface gráfica em **Swing**.
- CRUD completo de pets (nome, tipo, atributos, imagem).
- Botão para escolher qual pet é o “ativo” na tela principal.

### Tecnologias

- Java SE (JDK 17)
- JDBC
- SQLite (banco local em arquivo)
- Swing (GUI)

### Estrutura de pastas

```text
Tamagotchi/
├── database/
│   ├── tamagotchi.db           # Banco SQLite gerado em runtime
│   └── schema.sql              # Script para criar tabelas e seed
├── src/
│   ├── model/
│   │   └── Pet.java            # Modelo do bichinho (status + tipoUsuario + imagem)
│   ├── dao/
│   │   ├── ConnectionFactory.java
│   │   └── PetDAO.java
│   ├── controller/
│   │   └── GameController.java
│   └── view/
│       ├── MainFrame.java      # GUI principal (Swing)
│       ├── PetCrudDialog.java  # CRUD de pets (Swing)
│       └── GameView.java       # Versão console opcional
└── lib/
    └── sqlite-jdbc.jar         # Driver JDBC do SQLite (adicione aqui)
```

### Modelo de dados (SQLite)

#### Tabela `pet`

Guarda todos os Tamagotchis cadastrados.

| coluna       | tipo    | descrição                         |
|-------------|---------|-----------------------------------|
| id          | INTEGER | PK autoincrement                  |
| nome        | TEXT    | nome do pet                       |
| tipo_usuario| TEXT    | tipo definido pelo usuário        |
| hunger      | INTEGER | fome (0–100)                      |
| happiness   | INTEGER | felicidade (0–100)                |
| energy      | INTEGER | energia (0–100)                   |
| image_path  | TEXT    | caminho da imagem no disco        |

O arquivo `database/schema.sql`:

- Cria a tabela `pet` com as colunas acima.
- Insere um pet inicial:
  - `Tama` com tipo `"Ovo"` e atributos 50/50/50.

### Comportamento do app

- **MainFrame (GUI principal)**:
  - Mostra o pet ativo:
    - Nome.
    - Tipo (texto livre, vindo de `tipo_usuario`).
    - Barras de Fome, Felicidade, Energia.
    - Imagem (se `image_path` estiver preenchido e o arquivo existir).
  - Botões de ação:
    - `Alimentar`, `Brincar`, `Dormir`, `Exercitar`.
    - Cada ação altera os atributos (sempre entre 0 e 100) e salva no banco via `GameController` + `PetDAO`.
  - Botão **“Gerenciar Pets…”**:
    - Abre o `PetCrudDialog` (CRUD completo).

- **PetCrudDialog (CRUD)**:
  - Lista todos os pets em uma tabela: **ID, Nome, Tipo**.
  - Botões:
    - `Novo`: abre formulário para cadastrar pet (nome, tipo, atributos iniciais, imagem).
    - `Editar`: carrega o pet selecionado no formulário.
    - `Excluir`: remove o pet selecionado do banco.
    - `Usar este Pet`: define o pet selecionado como **pet ativo** no `MainFrame` (via `GameController.carregarPetPorId`).
  - Formulário de pet (`PetFormDialog`):
    - Campos:
      - Nome (`JTextField`).
      - Tipo (texto livre, `JTextField` → gravado em `tipo_usuario`).
      - Fome / Felicidade / Energia (`JSpinner` 0–100).
      - Imagem (`JTextField` + `JFileChooser` para escolher arquivo).

### Driver SQLite

1. Baixe o driver JDBC (ex.: `sqlite-jdbc-x.x.x.jar`).
2. Coloque em `lib/sqlite-jdbc.jar` (ou ajuste o nome no classpath).
3. Inclua esse `.jar` no classpath ao compilar/executar.

### Compilar e executar (terminal, Windows)

Na pasta `Tamagotchi`:

```powershell
# (Opcional) limpar banco se mudou schema: SE FOR MUDAR ALGO NO SCHEMA BASE TEM Q RODAR ESSE COMANDO 
del .\database\tamagotchi.db

# Compilar
javac -cp "lib\sqlite-jdbc.jar" -d out -sourcepath src `
  src\model\*.java src\dao\*.java src\controller\*.java src\view\*.java

# Executar GUI (ajuste o caminho do JDK 17 se preciso)
& "C:\Users*************\" view.MainFrame
```

### Resumo de uso

- Use **MainFrame** para:
  - Ver o status do pet ativo.
  - Alimentar, brincar, fazer dormir, exercitar.
- Use **Gerenciar Pets…** para:
  - Criar quantos Tamagotchis quiser (cada um com tipo e imagem diferentes).
  - Escolher qual Tamagotchi será o **ativo** (botão “Usar este Pet”).

