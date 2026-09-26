package solidexercicio10.repository;

import solidexercicio10.model.Dificuldade;

/**
 * Representa uma entrada imutável no ranking do jogo Missão Marte.
 * Utiliza o recurso 'record' do Java para garantir que os dados de uma partida 
 * finalizada não sejam alterados acidentalmente após o seu registro.
 *
 * @param name                 O nome do piloto que concluiu a missão.
 * @param score                A pontuação final calculada ao fim da partida.
 * @param dificuldade          O nível de dificuldade em que a missão foi jogada (FACIL, MEDIO, DIFICIL).
 * @param passageirosColetados O número total de passageiros resgatados com sucesso.
 * @param dataHora             A data e hora em que a partida foi salva (em formato de String).
 * @param tempoJogo            O tempo total de duração da partida, em segundos.
 */
public record RankingEntry(
    String name, 
    int score, 
    Dificuldade dificuldade,
    int passageirosColetados, 
    String dataHora, 
    long tempoJogo
) {}