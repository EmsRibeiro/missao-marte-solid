package solidexercicio10.presentation;

import solidexercicio10.model.Asteroide;
import solidexercicio10.model.Inimigo;
import solidexercicio10.model.Missao;
import solidexercicio10.model.Passageiro;

/**
 * Renderiza o ambiente do jogo, quase como criar uma interface visual, só que com carácteres.
 * Somente mostra os dados da missão, sem mexer no resto da Entidade.
 * Busca utilizar o princípio S do Solid.
 */
public class MapaRenderer {
    /**
     * Desenha a grade do mapa no console, varrendo as coordenadas de cima para baixo 
     * e da esquerda para a direita, posicionando as entidades conforme suas coordenadas atuais.
     *
     * @param missao     Objeto central contendo as listas de entidades (nave, passageiros, etc).
     * @param minX       Limite mínimo do eixo X (borda esquerda).
     * @param maxX       Limite máximo do eixo X (borda direita).
     * @param minY       Limite mínimo do eixo Y (borda inferior).
     * @param maxY       Limite máximo do eixo Y (borda superior).
     * @param score      Pontuação atual do jogador.
     * @param pilotoNome Nome do piloto fornecido pelo usuário.
     */
    public void desenhar(Missao missao, int minX, int maxX, int minY, int maxY, int score, String pilotoNome) {
        System.out.println("\n================================================================");
        System.out.printf("Piloto: %s | Pontuação: %d%n", pilotoNome, score);
        System.out.println("================================================================");

        System.out.print("     ");
        for (int x = minX; x <= maxX; x++) {
            System.out.printf("%4d", x);
        }
        System.out.println();
        // w diminui y: linhas crescentes preservam a orientação do original.
        for (int y = minY; y <= maxY; y++) {
            System.out.printf("%4d ", y);
            for (int x = minX; x <= maxX; x++) {
                String simbolo = "."; 

                // Define a plataforma de pouso na origem
                if (x == 0 && y == 0) {
                    simbolo = "L";
                }

                if (missao.getNave().getX() == x && missao.getNave().getY() == y) {
                    simbolo = missao.getNave().getSimbolo();
                } else {
                    for (Passageiro p : missao.getPassageiros()) {
                        if (p.getX() == x && p.getY() == y) {
                            simbolo = p.getSimbolo();
                            break;
                        }
                    }
                    
                    // Permite que asteróides se sobreponham ao espaço vazio ou à plataforma
                    if (simbolo.equals(".") || simbolo.equals("L")) {
                        for (Asteroide a : missao.getAsteroides()) {
                            if (a.getX() == x && a.getY() == y) {
                                simbolo = a.getSimbolo();
                                break;
                            }
                        }
                    }

                    // Permite que inimigos se sobreponham ao espaço vazio ou à plataforma
                    if (simbolo.equals(".") || simbolo.equals("L")) {
                        for (Inimigo i : missao.getInimigos()) {
                            if (i.getX() == x && i.getY() == y) {
                                simbolo = i.getSimbolo();
                                break;
                            }
                        }
                    }
                }
                System.out.printf("%4s", simbolo);
            }
            System.out.println();
        }
        System.out.println("Legenda: N=Nave, P=Passageiro, A=Asteroide, I=Inimigo, L=Plataforma, .=Vazio");
        for (Passageiro passageiro : missao.getPassageiros()) {
            System.out.printf(" - %s (%s) em (%d,%d)%n", passageiro.getNome(),
                    passageiro.getTipo(), passageiro.getX(), passageiro.getY());
        }
        System.out.println("================================================================\n");
    }
}
