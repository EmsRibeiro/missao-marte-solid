package solidexercicio10.repository;

import java.util.List;

public interface RankingRepository {
    void salvar(RankingEntry entrada);
    List<RankingEntry> listar();
    void limpar();
}