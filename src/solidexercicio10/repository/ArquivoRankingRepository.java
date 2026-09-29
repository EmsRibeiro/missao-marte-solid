package solidexercicio10.repository;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import solidexercicio10.model.Dificuldade;

/** Persistência do Top 5 em JSON, sem imprimir mensagens ou decidir vitória. */
public class ArquivoRankingRepository implements RankingRepository {
    private final Path caminhoArquivo;

    public ArquivoRankingRepository(String nomeArquivo) {
        caminhoArquivo = Path.of(nomeArquivo);
    }

    @Override
    public void salvar(RankingEntry entrada) throws IOException {
        List<RankingEntry> ranking = listar();
        ranking.add(entrada);
        ranking.sort(Comparator.comparingInt(RankingEntry::score).reversed());
        salvarJson(ranking.subList(0, Math.min(5, ranking.size())));
    }

    @Override
    public List<RankingEntry> listar() throws IOException {
        String conteudo;
        try {
            conteudo = Files.readString(caminhoArquivo, StandardCharsets.UTF_8);
        } catch (NoSuchFileException e) {
            return new ArrayList<>();
        }
        List<RankingEntry> ranking = new LeitorJson(conteudo).lerRanking();
        ranking.sort(Comparator.comparingInt(RankingEntry::score).reversed());
        return new ArrayList<>(ranking.subList(0, Math.min(5, ranking.size())));
    }

    @Override
    public void limpar() throws IOException {
        Files.deleteIfExists(caminhoArquivo);
    }

    private void salvarJson(List<RankingEntry> ranking) throws IOException {
        StringBuilder json = new StringBuilder("[\n");
        for (int i = 0; i < ranking.size(); i++) {
            RankingEntry r = ranking.get(i);
            json.append("  {\n")
                    .append("    \"name\": ").append(escapar(r.name())).append(",\n")
                    .append("    \"score\": ").append(r.score()).append(",\n")
                    .append("    \"dificuldade\": ").append(escapar(r.dificuldade().name())).append(",\n")
                    .append("    \"passageirosColetados\": ").append(r.passageirosColetados()).append(",\n")
                    .append("    \"dataHora\": ").append(escapar(r.dataHora())).append(",\n")
                    .append("    \"tempoJogo\": ").append(r.tempoJogo()).append("\n  }");
            json.append(i + 1 < ranking.size() ? ",\n" : "\n");
        }
        json.append("]\n");
        Files.writeString(caminhoArquivo, json, StandardCharsets.UTF_8);
    }

    private static String escapar(String texto) {
        StringBuilder resultado = new StringBuilder("\"");
        for (char c : texto.toCharArray()) {
            switch (c) {
                case '"' -> resultado.append("\\\"");
                case '\\' -> resultado.append("\\\\");
                case '\b' -> resultado.append("\\b");
                case '\f' -> resultado.append("\\f");
                case '\n' -> resultado.append("\\n");
                case '\r' -> resultado.append("\\r");
                case '\t' -> resultado.append("\\t");
                default -> {
                    if (c < 0x20) resultado.append(String.format("\\u%04x", (int) c));
                    else resultado.append(c);
                }
            }
        }
        return resultado.append('"').toString();
    }

    /** Leitor restrito ao formato do ranking: array de objetos com strings e inteiros. */
    private static final class LeitorJson {
        private final String texto;
        private int posicao;

        LeitorJson(String texto) {
            this.texto = texto;
        }

        List<RankingEntry> lerRanking() throws IOException {
            List<RankingEntry> ranking = new ArrayList<>();
            exigir('[');
            if (!consumir(']')) {
                do {
                    Map<String, String> campos = lerObjeto();
                    if (!campos.containsKey("name") || !campos.containsKey("score")) {
                        throw erro("Registro sem nome ou pontuação");
                    }
                    try {
                        ranking.add(new RankingEntry(campos.get("name"),
                                Integer.parseInt(campos.get("score")),
                                Dificuldade.deString(campos.get("dificuldade")),
                                Integer.parseInt(campos.getOrDefault("passageirosColetados", "0")),
                                campos.getOrDefault("dataHora", ""),
                                Long.parseLong(campos.getOrDefault("tempoJogo", "0"))));
                    } catch (NumberFormatException e) {
                        throw new IOException("Ranking JSON contém número inválido", e);
                    }
                } while (consumir(','));
                exigir(']');
            }
            ignorarEspacos();
            if (posicao != texto.length()) throw erro("Conteúdo após o fim do ranking");
            return ranking;
        }

        private Map<String, String> lerObjeto() throws IOException {
            Map<String, String> campos = new HashMap<>();
            exigir('{');
            if (consumir('}')) return campos;
            do {
                String chave = lerString();
                exigir(':');
                ignorarEspacos();
                String valor = posicao < texto.length() && texto.charAt(posicao) == '"'
                        ? lerString() : lerNumero();
                if (campos.putIfAbsent(chave, valor) != null) throw erro("Campo duplicado: " + chave);
            } while (consumir(','));
            exigir('}');
            return campos;
        }

        private String lerString() throws IOException {
            exigir('"');
            StringBuilder valor = new StringBuilder();
            while (posicao < texto.length()) {
                char c = texto.charAt(posicao++);
                if (c == '"') return valor.toString();
                if (c < 0x20) throw erro("Caractere de controle sem escape");
                if (c != '\\') {
                    valor.append(c);
                    continue;
                }
                if (posicao == texto.length()) throw erro("Escape incompleto");
                char escape = texto.charAt(posicao++);
                switch (escape) {
                    case '"', '\\', '/' -> valor.append(escape);
                    case 'b' -> valor.append('\b');
                    case 'f' -> valor.append('\f');
                    case 'n' -> valor.append('\n');
                    case 'r' -> valor.append('\r');
                    case 't' -> valor.append('\t');
                    case 'u' -> {
                        if (posicao + 4 > texto.length()) throw erro("Escape Unicode incompleto");
                        try {
                            valor.append((char) Integer.parseInt(texto.substring(posicao, posicao + 4), 16));
                        } catch (NumberFormatException e) {
                            throw erro("Escape Unicode inválido");
                        }
                        posicao += 4;
                    }
                    default -> throw erro("Escape inválido");
                }
            }
            throw erro("String sem fechamento");
        }

        private String lerNumero() throws IOException {
            ignorarEspacos();
            int inicio = posicao;
            if (posicao < texto.length() && texto.charAt(posicao) == '-') posicao++;
            int digitos = posicao;
            while (posicao < texto.length() && texto.charAt(posicao) >= '0'
                    && texto.charAt(posicao) <= '9') posicao++;
            if (posicao == digitos) throw erro("Número esperado");
            if (posicao - digitos > 1 && texto.charAt(digitos) == '0') throw erro("Zero inicial inválido");
            return texto.substring(inicio, posicao);
        }

        private void exigir(char esperado) throws IOException {
            if (!consumir(esperado)) throw erro("Esperado '" + esperado + "'");
        }

        private boolean consumir(char esperado) {
            ignorarEspacos();
            if (posicao < texto.length() && texto.charAt(posicao) == esperado) {
                posicao++;
                return true;
            }
            return false;
        }

        private void ignorarEspacos() {
            while (posicao < texto.length() && " \n\r\t".indexOf(texto.charAt(posicao)) >= 0) posicao++;
        }

        private IOException erro(String mensagem) {
            return new IOException("Ranking JSON inválido na posição " + posicao + ": " + mensagem);
        }
    }
}
