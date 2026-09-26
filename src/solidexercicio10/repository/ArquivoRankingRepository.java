package solidexercicio10.repository;

import solidexercicio10.model.Dificuldade;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

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
    public void salvar(RankingEntry entrada) {
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
    public List<RankingEntry> listar() {
        List<RankingEntry> lista = new ArrayList<>();
        if (!Files.exists(caminhoArquivo)) {
            return lista; 
        }

        try {
            String conteudo = Files.readString(caminhoArquivo, StandardCharsets.UTF_8);
            
            int start = conteudo.indexOf('{');
            while (start != -1) {
                int end = conteudo.indexOf('}', start);
                if (end == -1) break;
                
                String obj = conteudo.substring(start, end);

                String name = extrairString(obj, "\"name\"");
                int score = Integer.parseInt(extrairNumero(obj, "\"score\""));
                Dificuldade diff = Dificuldade.deString(extrairString(obj, "\"dificuldade\""));
                int pass = Integer.parseInt(extrairNumero(obj, "\"passageirosColetados\""));
                String data = extrairString(obj, "\"dataHora\"");
                long tempo = Long.parseLong(extrairNumero(obj, "\"tempoJogo\""));

                lista.add(new RankingEntry(name, score, diff, pass, data, tempo));
                
                start = conteudo.indexOf('{', end);
            }
        } catch (Exception e) {
            System.out.println("Erro ao ler ranking: " + e.getMessage());
        }
        return lista;
    }

    @Override
    public void limpar() {
        try {
            Files.deleteIfExists(caminhoArquivo);
            System.out.println("Ranking resetado com sucesso.");
        } catch (IOException e) {
            System.out.println("Erro ao tentar limpar o ranking: " + e.getMessage());
        }
    }

    private void salvarJson(List<RankingEntry> ranking) {
        StringBuilder sb = new StringBuilder();
        sb.append("[\n"); 
        
        for (int i = 0; i < ranking.size(); i++) {
            RankingEntry r = ranking.get(i);
            sb.append("  {\n");
            sb.append("    \"name\": \"").append(r.name()).append("\",\n");
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

        try {
            Files.writeString(caminhoArquivo, sb.toString(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.out.println("Erro ao salvar arquivo JSON: " + e.getMessage());
        }
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