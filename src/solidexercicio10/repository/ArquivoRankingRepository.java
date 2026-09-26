package solidexercicio10.repository;

import solidexercicio10.model.Dificuldade;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ArquivoRankingRepository implements RankingRepository {
    private final Path caminhoArquivo;

    public ArquivoRankingRepository(String nomeArquivo) {
        this.caminhoArquivo = Paths.get(nomeArquivo);
    }

    @Override
    public void salvar(RankingEntry entrada) {
        // le o json antigo, adc nova entrada ordena e corta somente os 5 pra salvar num arquivo novo
    }

    @Override
    public List<RankingEntry> listar() {
        // le o arquivo txt e forma o json
        return new ArrayList<>();
    }

    @Override
    public void limpar() {
        try {
            Files.deleteIfExists(caminhoArquivo);
            System.out.println("Ranking resetado com sucesso.");
        } catch (Exception e) {
            System.out.println("Erro ao tentar limpar o ranking: " + e.getMessage());
        }
    }
}