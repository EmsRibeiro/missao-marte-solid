package solidexercicio10.repository;

import java.io.IOException;
import java.util.List;

public interface RankingRepository {
    void salvar(RankingEntry entrada) throws IOException;
    List<RankingEntry> listar() throws IOException;
    void limpar() throws IOException;
}