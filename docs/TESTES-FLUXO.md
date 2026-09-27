# Revisão e testes do fluxo — Paulo

Revisão do fluxo iniciada em 26/09/2026 e repetida em 27/09/2026, após a
integração dos PRs #1, #2 e #3.

## Escopo da verificação

O código inteiro de `src/solidexercicio10/` foi compilado em Java 17 com as
implementações reais do modelo, repositório e renderizador. O teste executável
usa o renderizador real e substitui somente o repositório de arquivo por um
repositório em memória, para testar o fluxo sem alterar o ranking do jogador.

Um teste adicional de integração, executado em diretório temporário, venceu
uma partida com posições controladas, gravou uma entrada real no JSON, abriu
uma segunda execução para consultar a pontuação 82 e resetou o arquivo. O
teste também confirmou estatísticas ao abandonar. O código desse teste
adicional era temporário; `tests/JogoServiceTest.java` cobre o fluxo de forma
repetível no repositório entregue.

## Resultados

O teste executável `tests/JogoServiceTest.java` passou nos seguintes cenários:

- vitória com cinco passageiros e retorno a `(0,0)`;
- pontuação final de 82 para uma rota de oito movimentos no modo médio;
- resgatar todos sem pousar não registra vitória nem ranking;
- empate com a quinta pontuação não substitui os registros anteriores;
- pontuação maior que a quinta é enviada ao repositório;
- consulta do ranking, confirmação e cancelamento do reset;
- dimensão inválida usa o tamanho padrão 5;
- capacidade de cinco lugares, inclusive no modo fácil;
- abandono da missão mostra estatísticas e não registra pontuação;
- três colisões encerram a missão e mostram estatísticas;
- pontuação zero encerra a missão sem registrar ranking;
- fim da entrada durante menu, preparação ou partida encerra sem loop infinito;
- `UncheckedIOException` ao listar, salvar ou limpar é informada ao jogador;
- falha ao salvar/limpar não exibe a mensagem de sucesso da operação.

O código original também compilou com Java 17. A comparação com a base
confirmou que nenhum arquivo em `src/exercicio10/` foi alterado.

## Como repetir no projeto integrado

Na raiz do repositório, depois de compilar todos os arquivos da versão nova:

```bash
javac -encoding UTF-8 -cp out -d out tests/JogoServiceTest.java
java -cp out JogoServiceTest
```

Os dois comandos também funcionam no PowerShell. O teste usa um repositório
em memória para não alterar o ranking do jogador. Os dados aleatórios são
controlados para que a mesma rota tenha sempre o mesmo resultado.

## Ajustes feitos nesta revisão

1. Estatísticas passam a aparecer na vitória, derrota, abandono e fim da entrada
   durante a partida. Isso atende ao requisito de estatísticas ao final da missão.
2. A capacidade da nave voltou a cinco, como no código original.
3. O menu informa erros de I/O representados por `UncheckedIOException` e permite
   nova tentativa. A implementação atual do repositório de arquivo captura e
   imprime suas falhas sem lançar exceção, portanto a indicação de sucesso
   ainda depende de correção na camada de persistência.

## Decisões e limitações explícitas

- O tamanho aceito é de 2 a 50 (coordenadas `-tamanho` a `+tamanho`). O mínimo
  garante espaço para todas as dificuldades; o máximo evita mapas impraticáveis
  no console. Esse limite é uma mudança deliberada em relação ao original.
- Cada comando de movimento gasta um ponto, inclusive quando a nave já está
  no limite; um comando de embarque avança os inimigos mesmo sem passageiro.
  Ambos os comportamentos foram mantidos do original.
- O modo fácil mantém quatro passageiros (dois professores e dois engenheiros),
  e os demais mantêm cinco, incluindo o astronauta, conforme o código original.
- O serviço ainda imprime menu e mensagens. A separação de apresentação é parcial;
  isso pode ser discutido na revisão crítica de SRP.
- A pontuação usa polimorfismo; o catálogo em `criarPassageiro` ainda precisa
  ser editado para incluir um novo tipo. OCP é parcial, não absoluto.

## Pendências de qualidade

- O renderizador real não mostra o símbolo da plataforma `L` em `(0,0)` quando
  a nave deixa a posição inicial.
- O repositório não escapa aspas em nomes no JSON e não propaga falhas reais
  de I/O; um reset malsucedido pode resultar em mensagem final de sucesso.
- Confirmar com o responsável pelo ranking a ordenação e o Top 5 em arquivo
  real depois de corrigir casos inválidos.
- Finalizar `REVISAO-SOLID.md` e complementar os diagramas UML.
