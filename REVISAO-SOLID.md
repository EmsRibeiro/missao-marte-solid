# Revisão SOLID - Missão Marte
**Aluno:** Jully Emerson Ribeiro Costa (2427275) - ADS / Unifor
**Módulo:** Apresentação e Persistência

## 1. Análise dos Princípios SOLID Aplicados

**1. SRP (Princípio da Responsabilidade Única):**
*   **Apresentação e Persistência:** A classe `MapaRenderer` tem a única responsabilidade de desenhar o mapa, sem calcular colisões ou mover a nave. O `ArquivoRankingRepository` foca exclusivamente em ler e gravar o arquivo físico. A classe `RankingEntry` é um *record* imutável (DTO) que apenas transporta os dados de forma segura.
*   **Tratamento de Erros e JSON:** A correção do parser de JSON (bug das aspas) foi feita isoladamente no repositório, sem que o *Model* ou o *Service* precisassem ser alterados. O tratamento de exceções no `JogoService` foi refatorado para ocorrer dentro de cada função específica, evitando que o menu principal acumulasse a responsabilidade de tratar erros de infraestrutura.

**2. OCP (Princípio Aberto/Fechado):**
Graças à interface `RankingRepository`, nosso sistema de persistência está aberto para expansão. Caso seja necessário salvar o ranking em um Banco de Dados no futuro, basta criar uma nova classe implementando a interface, sem modificar e arriscar quebrar o `JogoService`.

**3. LSP (Princípio da Substituição de Liskov):**
A implementação de arquivo age de forma previsível e respeita o contrato. Anteriormente, a classe "engolia" erros de leitura/escrita. Ao adotarmos o lançamento explícito de `IOException`, a implementação passou a respeitar o contrato do mundo real: operações de disco podem falhar e a aplicação deve ser notificada.

**4. ISP (Princípio da Segregação da Interface):**
A interface `RankingRepository` é enxuta, possuindo apenas os três métodos estritamente necessários (`salvar`, `listar`, `limpar`). Ela não força as implementações a lidarem com métodos inúteis para a persistência.

**5. DIP (Princípio da Inversão de Dependência):**
A regra de negócio (`JogoService`) não depende do módulo de baixo nível (`ArquivoRankingRepository`), mas sim da abstração `RankingRepository`. O serviço não sabe se a falha no armazenamento vem de um JSON ou de um Banco de Dados, ele apenas trata a falha genérica de persistência ditada pelo contrato da interface.

## 2. Melhorias Realizadas e Justificativas

*   **Tratamento de Exceções de I/O (Breaking Change):** Removemos os blocos `try-catch` silenciosos do repositório, forçando a classe `JogoService` a capturar as falhas. Agora, se o disco estiver cheio, o erro não é omitido; o jogador recebe um aviso amigável, prevenindo falhas silenciosas.
*   **Leitura Resiliente de JSON:** Mantive a decisão técnica de manipular o JSON de forma nativa e sem bibliotecas externas (como Gson ou Jackson) para simplificar a compilação via terminal. Desenvolvemos um parser inteligente (`extrairString`) capaz de ignorar aspas escapadas (`\"`), corrigindo a corrupção de dados ao ler nomes complexos.

## 3. Desafios e Reflexões Pessoais

Durante a refatoração, enfrentei desafios significativos que agregaram muito ao meu aprendizado:
1.  **Refatoração e Tratamento de Escopo:** Reconstruir as funções do serviço aplicando `try-catch` de forma independente exigiu perspicácia e cuidado para não quebrar o fluxo do sistema. Foi um processo trabalhoso identificar os gargalos técnicos e delegar o tratamento de `IOException` corretamente.
2.  **Internalização do DIP:** Aplicar a Inversão de Dependência na prática foi um paradigma novo. Compreender que o `RankingRepository` funciona apenas como um leitor genérico, que não depende de outras partes do jogo para funcionar, mudou a minha visão sobre o desacoplamento de arquitetura de software.

## 4. Resultados dos Testes
A aplicação compila com sucesso nativamente no JDK. Foram realizados testes de fluxo cruzado integrando com sucesso as lógicas de estatísticas com o tratamento de I/O, além de testes práticos de estresse no parser de JSON com caracteres especiais, confirmando a robustez da persistência.
