package solidexercicio10.repository;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import solidexercicio10.model.Dificuldade;

/**
 * Implementação concreta de {@link RankingRepository} para armazenamento de pontuações em arquivo físico.
 * <p>
 * Esta classe é responsável por ler, gravar e gerenciar as entradas do ranking do jogo,
 * persistindo os dados no formato JSON sem a necessidade de bibliotecas externas.
 * O arquivo mantém, por regra de negócio, apenas as 5 maiores pontuações (Top 5),
 * ordenadas de forma decrescente.
 * </p>
 */
public class ArquivoRankingRepository implements RankingRepository {
    private final Path caminhoArquivo;

    public ArquivoRankingRepository(String nomeArquivo) {
        this.caminhoArquivo = Paths.get(nomeArquivo);
    }

    @Override
    public void salvar(RankingEntry entrada) throws IOException {
        List<RankingEntry> ranking = listar();
        ranking.add(entrada);

        // Ordena a lista em ordem decrescente (do maior score para o menor)
        ranking.sort((r1, r2) -> Integer.compare(r2.score(), r1.score()));

        // Limita o ranking aos 5 melhores, conforme o contrato
        if (ranking.size() > 5) {
            ranking = ranking.subList(0, 5);
        }

        salvarJson(ranking);
    }

    @Override
    public List<RankingEntry> listar() throws IOException {
        List<RankingEntry> lista = new ArrayList<>();
        if (!Files.exists(caminhoArquivo)) {
            return lista; 
        }

        String conteudo = Files.readString(caminhoArquivo, StandardCharsets.UTF_8);
        
        int start = conteudo.indexOf('{');
        while (start != -1) {
            int end = conteudo.indexOf('}', start);
            if (end == -1) break;
            
            String obj = conteudo.substring(start, end);

            // Restaura o nome desfazendo o escape das aspas
            String name = extrairString(obj, "\"name\"").replace("\\\"", "\"");
            int score = Integer.parseInt(extrairNumero(obj, "\"score\""));
            Dificuldade diff = Dificuldade.deString(extrairString(obj, "\"dificuldade\""));
            int pass = Integer.parseInt(extrairNumero(obj, "\"passageirosColetados\""));
            String data = extrairString(obj, "\"dataHora\"");
            long tempo = Long.parseLong(extrairNumero(obj, "\"tempoJogo\""));

            lista.add(new RankingEntry(name, score, diff, pass, data, tempo));
            
            start = conteudo.indexOf('{', end);
        }
        return lista;
    }

    @Override
    public void limpar() throws IOException {
        Files.deleteIfExists(caminhoArquivo);
    }

    private void salvarJson(List<RankingEntry> ranking) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("[\n"); 
        
        for (int i = 0; i < ranking.size(); i++) {
            RankingEntry r = ranking.get(i);
            
            // Escapa as aspas no nome para garantir que o JSON gerado seja válido
            String nomeEscapado = r.name().replace("\"", "\\\"");

            sb.append("  {\n");
            sb.append("    \"name\": \"").append(nomeEscapado).append("\",\n");
            sb.append("    \"score\": ").append(r.score()).append(",\n");
            sb.append("    \"dificuldade\": \"").append(r.dificuldade()).append("\",\n");
            sb.append("    \"passageirosColetados\": ").append(r.passageirosColetados()).append(",\n");
            sb.append("    \"dataHora\": \"").append(r.dataHora()).append("\",\n");
            sb.append("    \"tempoJogo\": ").append(r.tempoJogo()).append("\n");
            sb.append("  }");
            
            if (i < ranking.size() - 1) {
                sb.append(",");
            }
            sb.append("\n");
        }
        sb.append("]"); 

        Files.writeString(caminhoArquivo, sb.toString(), StandardCharsets.UTF_8);
    }

    private String extrairString(String json, String chave) {
        int idx = json.indexOf(chave);
        if (idx == -1) return "";
        int startQuote = json.indexOf("\"", idx + chave.length() + 1);
        int endQuote = json.indexOf("\"", startQuote + 1);
        if (startQuote == -1 || endQuote == -1) return "";
        return json.substring(startQuote + 1, endQuote);
    }

    private String extrairNumero(String json, String chave) {
        int idx = json.indexOf(chave);
        if (idx == -1) return "0";
        int doisPontos = json.indexOf(":", idx);
        int virgula = json.indexOf(",", doisPontos);
        if (virgula == -1) virgula = json.length(); 
        
        String valor = json.substring(doisPontos + 1, virgula).trim();
        return valor.replace("\"", ""); 
    }
}