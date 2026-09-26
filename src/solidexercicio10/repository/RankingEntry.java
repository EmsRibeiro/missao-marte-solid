package solidexercicio10.repository;

import solidexercicio10.model.Dificuldade;

public record RankingEntry(
    String name, 
    int score, 
    Dificuldade dificuldade,
    int passageirosColetados, 
    String dataHora, 
    long tempoJogo
) {}