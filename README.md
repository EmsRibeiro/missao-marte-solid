# Missão Marte Unifor — refatoração SOLID

Base da atividade: [solid-tutorial](https://github.com/marcelobezerra-dotcom/solid-tutorial).
Repositório da equipe: https://github.com/paulo-edvandro/missao-marte-solid.

Paulo Edvandro Rocha Filho desenvolveu o fluxo e a integração inicial; Lucas
Alencar desenvolveu as entidades do modelo; Emerson (`EmsRibeiro`) desenvolveu
a persistência e o desenho do mapa. Cada integrante deve identificar sua
contribuição e adaptar este README no repositório individual entregue.

O código inicial em `src/exercicio10/` permanece preservado para comparação.
O jogo refatorado está em `src/solidexercicio10/`. As assinaturas combinadas
entre os integrantes estão em [docs/CONTRATOS.md](docs/CONTRATOS.md).

## Compilar e executar

É necessário JDK 17 ou superior. Execute os comandos na raiz do repositório.

### Git Bash ou terminal Linux/macOS

```bash
mkdir -p out
javac -encoding UTF-8 -d out $(find src/solidexercicio10 -name '*.java')
java -cp out solidexercicio10.Main
```

### PowerShell

```powershell
New-Item -ItemType Directory -Force out | Out-Null
javac -encoding UTF-8 -d out (Get-ChildItem -Recurse -Filter *.java src/solidexercicio10 | ForEach-Object FullName)
java -cp out solidexercicio10.Main
```

Para executar a versão original no Git Bash:

```bash
javac -encoding UTF-8 -d out src/exercicio10/*.java
java -cp out exercicio10.Main
```

O original grava `ranking.json`; a versão nova grava
`ranking-solid-exercicio10.json` no diretório de execução.

O menu permite iniciar uma missão, consultar o Top 5, limpar o ranking com
confirmação ou sair. Na partida, use `w`, `a`, `s`, `d` para mover, `c` para
embarcar um passageiro na mesma posição e `q` para abandonar. Para vencer,
é necessário embarcar todos os passageiros e retornar à posição `(0,0)`.
O tamanho informado para o mapa deve estar entre 2 e 50; entradas inválidas
usam o tamanho 5.

## Organização e decisões

- `Main` monta as dependências e inicia o loop de leitura.
- `model` guarda as entidades e regras da missão. As subclasses de `Passageiro`
  definem suas pontuações por meio de `getPontuacao()`.
- `service/JogoService` conduz o menu e a partida, pede ao modelo para mover,
  embarcar e verificar colisões, e usa o contrato `RankingRepository` para
  consultar e atualizar o ranking.
- `presentation/MapaRenderer` desenha o mapa; `repository` contém a interface
  do ranking, o registro de cada partida e sua implementação em arquivo JSON.

O [diagrama de classes](docs/uml/diagrama-classes-model.svg) mostra entidades,
herança, interfaces e associações do modelo; sua
[fonte PlantUML](docs/uml/diagrama-classes-model.puml) permite alterações.
O [diagrama de pacotes](docs/uml/diagrama-pacotes.mmd) registra as dependências
entre as camadas; falta exportar uma imagem do diagrama de pacotes.

## Verificações e limitações

O teste de fluxo em `tests/JogoServiceTest.java` utiliza posições controladas
e um ranking em memória. Para executá-lo após compilar a versão nova:

```bash
javac -encoding UTF-8 -cp out -d out tests/JogoServiceTest.java
java -cp out JogoServiceTest
```

Os resultados e os testes manuais de persistência estão registrados em
[docs/TESTES-FLUXO.md](docs/TESTES-FLUXO.md) e
[docs/TESTES-MODELO.md](docs/TESTES-MODELO.md).

- A plataforma `L` em `(0,0)` ainda precisa aparecer no desenho do mapa quando
  a nave estiver em outra posição.
- O ranking atual não escapa caracteres especiais do nome no JSON e não
  propaga falhas reais de leitura, gravação ou remoção; a mensagem de erro do
  serviço só funciona quando o repositório sinaliza a falha.
- O serviço ainda imprime mensagens diretamente; a separação da apresentação
  está incompleta. O cadastro de tipos de passageiros ainda exige editar
  `JogoService.criarPassageiro`, o que limita o OCP.
- O diagrama de classes deve incluir `Dificuldade`. Ainda falta criar a
  revisão crítica obrigatória `REVISAO-SOLID.md`.

Antes da entrega individual, cada integrante deve revisar as limitações
acima, adicionar seu nome e suas decisões ao README, conferir os diagramas
e incluir a sua revisão crítica do projeto.
