# Revisão e testes do fluxo — Paulo

Revisão de 26/09/2026, na branch `refactor/fluxo`.

## Escopo da verificação

O `Main` e o `JogoService` foram compilados em Java 17 com o modelo do PR #1
de Lucas (commit `1948fc186c813b47ec58aa1bd17aedaecc5c0520`). Para os pacotes
de Emerson, ainda não integrados, foram usadas implementações temporárias
com as assinaturas de `docs/CONTRATOS.md`: ranking em memória e renderizador
sem desenho. Essas implementações temporárias não fazem parte do código entregue.

Portanto, estes resultados verificam o fluxo e sua integração com o modelo,
mas não validam a leitura/escrita do JSON nem a aparência do mapa final.

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

O código original também compilou com Java 17. A comparação com `main`
confirmou que nenhum arquivo em `src/exercicio10/` foi alterado.

## Como repetir após integrar as três partes

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
   nova tentativa. Conferir o tratamento de erros da implementação real de Emerson
   durante a integração; o contrato não especificava o tipo da exceção.

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

## Pendências de integração

- Compilar `main` depois de integrar os PRs e repetir este teste.
- Executar uma partida com o renderizador real, verificando símbolos e limites.
- Salvar uma vitória, fechar o programa e reabri-lo para validar o JSON.
- Confirmar ordenação, limite Top 5 e reset no arquivo real.
- Conferir o comportamento quando o arquivo não existe ou não pode ser acessado.
- Finalizar `REVISAO-SOLID.md`, README e conferir os dois diagramas UML.
