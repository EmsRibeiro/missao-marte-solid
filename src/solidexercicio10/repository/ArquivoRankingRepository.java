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
        // Aqui vamos colocar a lógica para ler o JSON antigo, adicionar a nova entrada,
        // ordenar, cortar os 5 primeiros e salvar o JSON novo.
    }

    @Override
    public List<RankingEntry> listar() {
        // Aqui vamos ler o arquivo de texto e transformar o JSON de volta para a lista.
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