# Contexto Do Projeto Tamagotchi

## Local Do Projeto

Raiz do repositorio:

```text
C:\Users\Usuario\Desktop\java implements\testPOO
```

Raiz real da aplicacao:

```text
C:\Users\Usuario\Desktop\java implements\testPOO\Tamagotchi
```

## Stack

- Java Swing
- SQLite
- JDBC
- Driver local: `Tamagotchi/lib/sqlite-jdbc.jar`
- JDK usado para compilar/rodar nesta maquina:

```text
C:\Program Files\Android\openjdk\jdk-21.0.8
```

O `java` padrao do PATH e Java 8, entao use sempre o JDK 21 acima para compilar e executar.

## Como Rodar

```powershell
cd "C:\Users\Usuario\Desktop\java implements\testPOO\Tamagotchi"
& "C:\Program Files\Android\openjdk\jdk-21.0.8\bin\javac.exe" -cp "lib\sqlite-jdbc.jar" -d out -sourcepath src src\model\*.java src\dao\*.java src\controller\*.java src\view\*.java
& "C:\Program Files\Android\openjdk\jdk-21.0.8\bin\java.exe" -cp "lib\sqlite-jdbc.jar;out" view.MainFrame
```

Console:

```powershell
Write-Output 0 | & "C:\Program Files\Android\openjdk\jdk-21.0.8\bin\java.exe" -cp "lib\sqlite-jdbc.jar;out" view.GameView
```

Processo Java mais recente aberto:

```text
PID 11864
```

## Banco De Dados

Banco local:

```text
Tamagotchi/database/tamagotchi.db
```

Schema:

```text
Tamagotchi/database/schema.sql
```

Tabelas importantes:

- `pet`
- `pet.image_data BLOB`: imagem salva em bytes no banco.
- `pet.last_needs_update_epoch`: timestamp do ultimo decaimento de necessidades.
- `pet.healthy_minutes`: historico agregado para evolucao futura.
- `pet.care_count`: contador agregado de cuidados.
- `pet.neglect_minutes`: tempo agregado de negligencia.
- `pet_care_history`: eventos de cuidado/decaimento/evolucao para evolucao futura.
- `app_setting`
- `app_setting.active_pet_id`: id do pet ativo persistido.
- `creature_type`
- `evolution`

O backend aceita diretorio customizado para o banco:

- Variavel: `TAMAGOTCHI_DATABASE_DIR`
- Propriedade JVM: `-Dtamagotchi.database.dir=...`

## Estado Atual Da Aplicacao

### Sistema De Necessidades

Arquivos principais:

- `Tamagotchi/src/controller/GameController.java`
- `Tamagotchi/src/model/Pet.java`
- `Tamagotchi/src/dao/PetDAO.java`
- `Tamagotchi/src/dao/ConnectionFactory.java`
- `Tamagotchi/database/schema.sql`

Regras atuais:

- Atributos variam de `0` a `100`.
- Novos pets iniciam em `100/100/100`.
- `Fome` significa saciedade/alimentacao:
  - `100`: completamente alimentado.
  - `0`: extremamente faminto.
- Decaimento por tempo:
  - fome: `-1` a cada 5 minutos.
  - felicidade: `-1` a cada 10 minutos.
  - energia: `-1` a cada 8 minutos.
- Penalidades por necessidades baixas:
  - se fome `< 20`: felicidade perde `-2` adicionais por ciclo e energia perde `-1` adicional.
  - se energia `< 20`: felicidade perde `-2` adicionais por ciclo.
- Acoes:
  - `Alimentar`: `+20 fome`.
  - `Brincar`: `-4 fome`, `+felicidade`, `-5 energia`.
  - `Dormir`: `-3 fome`, `+2 felicidade`, `+30 energia`.
  - `Exercitar`: `-8 fome`, `+felicidade`, `-10 energia`; bloqueado se energia estiver critica.
- Estado geral e calculado pela media de fome, felicidade e energia:
  - `90-100`: Radiante
  - `70-89`: Feliz
  - `50-69`: Bem
  - `30-49`: Cansado
  - `15-29`: Triste
  - `0-14`: Critico
- A UI chama `controller.atualizarNecessidadesPorTempo()` periodicamente enquanto aberta.

### Evolucao

- Evolucao existente continua usando tabela `evolution`.
- `GameController.checkEvolution()` considera felicidade, fome/saciedade e media geral.
- O historico `pet_care_history`, `healthy_minutes`, `care_count` e `neglect_minutes` foi preparado para evolucao futura mais sofisticada.

### UI/UX

Arquivos principais:

- `Tamagotchi/src/view/Theme.java`
- `Tamagotchi/src/view/MainFrame.java`
- `Tamagotchi/src/view/PetCrudDialog.java`

Estado visual atual:

- Console Tamagotchi cyberpunk premium.
- Barra superior funcional com:
  - botao menu lateral.
  - titulo central `[ TAMA ]`.
  - relogio digital.
  - botao de configuracoes.
- Sidebar interna retratil com temas:
  - Pixel Retro
  - Cyber Neon
  - Cozy Nature
- Temas alternativos funcionam em tempo de execucao, sem persistencia no banco.
- Painel visual de configuracoes preparado para opcoes futuras.
- Area do pet em formato de monitor digital integrado ao console.
- Placeholder sem imagem com slot visual e subtexto.
- Barras de status neon animadas:
  - brilho/scan visual.
  - interpolacao do valor antigo ate o valor novo ao aumentar/diminuir.
- Botoes com glow, icones desenhados e feedback hover/pressed.
- Botao `GERENCIAR PETS` continua sendo o CTA principal.
- Render de imagem do pet:
  - imagens normais usam interpolacao bicubica/qualidade alta.
  - sprites pequenos ampliados preservam pixel art com nearest-neighbor.

### Backend/SQLite

Correcoes ja feitas:

- Corrigido erro de caminho que criava/lia `Tamagotchi/Tamagotchi/database`.
- Removido banco vazio gerado no caminho errado.
- Confirmado que `image_data BLOB` existe no banco real.
- `ConnectionFactory` procura banco/schema com caminhos robustos.
- `ConnectionFactory` aceita `TAMAGOTCHI_DATABASE_DIR` e `-Dtamagotchi.database.dir`.
- Adicionado fallback para schema como resource stream.
- Adicionada tabela `app_setting`.
- Persistido `active_pet_id`.
- Adicionada migracao para colunas do sistema de necessidades.
- Adicionada tabela `pet_care_history`.
- `PetDAO.listAll()` nao carrega BLOB pesado desnecessariamente.
- `PetDAO.update/delete` validam linhas afetadas.
- `PetDAO` normaliza tipo quando bate com `creature_type` sem bloquear tipo livre.
- `CreatureDAO` ordena regras de evolucao.

Validacoes feitas:

- Compilacao com JDK 21.
- Execucao da GUI Swing.
- Banco existente com migracoes.
- Banco novo via schema.
- CRUD via DAO.
- BLOB salvo e relido.
- Pet ativo persistido.
- Execucao a partir de diretorio de trabalho externo.

## Restricoes Importantes

Antes de alterar regras de negocio:

- Confirmar impacto em `GameController`.
- Validar que os valores continuam entre `0` e `100`.
- Recompilar com JDK 21.
- Evitar quebrar compatibilidade com o banco existente; novas colunas/tabelas precisam de migracao em `ConnectionFactory`.

Antes de alterar UI:

- Preferir alterar `src/view`.
- Nao mexer em DAO/controller/model quando a mudanca for apenas visual.
- Recompilar `out`.
- Se criar novas preferencias visuais, nao persistir no banco sem pedido explicito.

Antes de alterar banco:

- Atualizar `database/schema.sql`.
- Atualizar migracao em `ConnectionFactory` para bancos existentes.
- Preservar BLOB de imagem e `active_pet_id`.

## Arquivos Com Mudancas Pendentes No Git

Ha alteracoes em:

- `Tamagotchi/src/...`
- `Tamagotchi/out/...`
- `Tamagotchi/database/schema.sql`
- `Tamagotchi/database/tamagotchi.db`
- `Tamagotchi/README.md`
- `context.md`
- `README.md` removido da raiz `testPOO`

## Observacoes

- O projeto real e `Tamagotchi/`.
- A raiz `testPOO/` deve conter contexto/repositorio, sem README duplicado.
- Quando abrir a GUI, use sempre `view.MainFrame`.
- Para depurar mecanicas sem UI, use `view.GameView`.
