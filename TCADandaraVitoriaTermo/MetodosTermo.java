import java.util.Scanner;
import java.util.Random;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.Normalizer;
import java.util.stream.Collectors;
import java.util.List;

public class MetodosTermo {

    final static Scanner LER = new Scanner(System.in);
    final static Random sortear = new Random();

    public static String lerTexto() {
        String texto = LER.nextLine();
        return texto;
    }

    public static void voltarAoTopo() {
        System.out.print("\u001B[H");
    }

    public static void limparAbaixo() {
        System.out.print("\u001B[J");
    }

    public static void pausarMensagem(int milissegundos) {
        try {
            Thread.sleep(milissegundos);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static String minusculotirarAcento(String texto) {
        String minusculoSemAcento = Normalizer.normalize(texto.trim(), Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        return minusculoSemAcento.toLowerCase();
    }

    public static void mudarCor(int cor) {
        if (cor >= 0 && cor <= 255) {
            System.out.print("\033[38;5;" + cor + "m");
        } else {
            System.out.print("\033[39m"); // cor padrão do terminal
        }
    }

    public static void mudarCorFundo(int cor) {
        if (cor >= 0 && cor <= 255) {
            System.out.print("\033[48;5;" + cor + "m");
        } else {
            System.out.print("\033[49m"); // cor padrão do terminal
        }
    }

    public static final int VERDE = 72;
    public static final int AMARELO = 179;
    public static final int CINZA = 238;
    public static final int VERMELHO = 160;
    public static final int AMARELO_GTA = 220;
    public static final int COR_PADRAO = -1;

    public static final int TENTATIVAS = 6;
    public static final int TAM_LETRAS = 5;

    public static String sortearPalavraSecreta() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                MetodosTermo.class.getResourceAsStream("/BDPalavras.txt")))) {
            List<String> palavras = reader.lines().collect(Collectors.toList());
            String palavraSecreta = palavras.get(sortear.nextInt(palavras.size()));
            return palavraSecreta;
        } catch (Exception e) {
            e.printStackTrace();
            return "Não consegui sortear a palavra secreta!";
        }
    }

    public static int[] calcularCores(String palavraSecreta, String chute) {
        int[] cores = new int[TAM_LETRAS];
        boolean[] existe = new boolean[TAM_LETRAS];

        for (int i = 0; i < TAM_LETRAS; i++) {
            if (chute.charAt(i) == palavraSecreta.charAt(i)) {
                cores[i] = VERDE;
                existe[i] = true;
            } else {
                cores[i] = CINZA;
            }
        }
        // amarelo, arara e termo
        for (int i = 0; i < TAM_LETRAS; i++) {
            if (cores[i] == VERDE) {
                continue;
            }
            for (int j = 0; j < TAM_LETRAS; j++) {
                if (!existe[j] && chute.charAt(i) == palavraSecreta.charAt(j)) {
                    cores[i] = AMARELO;
                    existe[j] = true;
                    break;
                }
            }
        }
        return cores;
    }

    public static void sortearLetraDica(boolean[] dicas) {
        int posicaoLetraDica;
        do {
            posicaoLetraDica = sortear.nextInt(TAM_LETRAS);
        } while (dicas[posicaoLetraDica]);
        dicas[posicaoLetraDica] = true;

    }

    public static void imprimirQuadradinhos(int[][] cores, char[][] letrasDigitadas,
            boolean[] dicas, String palavraSecreta, int linhaChute) {
        String[] quadrado = {
                "┌────┐",
                "│ %C  │",
                "└────┘"
        };

        for (int i = 0; i < letrasDigitadas.length; i++) {
            for (int linha = 0; linha < 3; linha++) {
                for (int j = 0; j < letrasDigitadas[i].length; j++) {
                    char letra = letrasDigitadas[i][j];
                    int cor = cores[i][j];

                    if (i == linhaChute && dicas[j]) {
                        letra = Character.toUpperCase(palavraSecreta.charAt(j));
                        cor = VERDE;
                    }

                    mudarCorFundo(cor);

                    System.out.print(String.format(quadrado[linha], letra));

                    mudarCorFundo(COR_PADRAO);

                    System.out.print(" ");
                }
                System.out.println();
            }
            System.out.println();
        }
    }

    public static boolean jogarRodada(String palavraSecreta) {
        char[][] letrasDigitadas = new char[TENTATIVAS][TAM_LETRAS];
        int[][] cores = new int[TENTATIVAS][TAM_LETRAS];

        // limpa o que foi salvo na rodada passada
        for (int i = 0; i < TENTATIVAS; i++) {
            for (int j = 0; j < TAM_LETRAS; j++) {
                letrasDigitadas[i][j] = ' ';
                cores[i][j] = COR_PADRAO;
            }
        }

        boolean[] dicas = new boolean[TAM_LETRAS];
        int dicasUsadas = 0;
        int linhaChute = 0;

        System.out.print("\u001B[2J\u001B[H");
        System.out.flush();

        while (linhaChute < TENTATIVAS) {
            voltarAoTopo();
            System.out.println("             T E R M O ");
            System.out.println();
            imprimirQuadradinhos(cores, letrasDigitadas, dicas, palavraSecreta, linhaChute);
            System.out.println("Digite seu chute ou 'd' para pedir uma dica");
            limparAbaixo();
            String digitada = minusculotirarAcento(lerTexto());

            switch (digitada) {

                case "d":
                    if (dicasUsadas >= 2) {
                        System.out.println();
                        System.out.println("Suas dicas já se esgotaram!");
                        pausarMensagem(2500);

                    } else if (dicasUsadas == 0) {
                        sortearLetraDica(dicas);
                        dicasUsadas++;
                        System.out.println();
                        System.out.println("É sua primeira dica. Não serão gastas tentativas!");
                        pausarMensagem(3000);

                    } else if (linhaChute == TENTATIVAS - 1) {
                        System.out.println();
                        System.out.println("Você não pode receber a segunda dica, pois é sua última tentativa.");
                        pausarMensagem(3000);

                    } else {
                        sortearLetraDica(dicas);
                        dicasUsadas++;
                        for (int i = 0; i < TAM_LETRAS; i++) {
                            letrasDigitadas[linhaChute][i] = ' ';
                            cores[linhaChute][i] = CINZA;
                        }
                        linhaChute++;
                        System.out.println();
                        System.out.println("Você ganhou uma dica, mas perdeu uma tentativa.");
                        pausarMensagem(3000);
                    }
                    continue;

                default:
                    if (digitada.length() != TAM_LETRAS) {
                        mudarCor(VERMELHO);
                        System.out.println();
                        System.out.println("Você digitou errado!");
                        mudarCor(COR_PADRAO);
                        pausarMensagem(2500);
                        continue;
                    }
                    break;
            }

            int[] coresTentativa = calcularCores(palavraSecreta, digitada);

            for (int j = 0; j < TAM_LETRAS; j++) {
                letrasDigitadas[linhaChute][j] = Character.toUpperCase(digitada.charAt(j));
                cores[linhaChute][j] = coresTentativa[j];
            }

            if (digitada.equals(palavraSecreta)) {
                voltarAoTopo();
                System.out.println("             T E R M O ");
                System.out.println();
                imprimirQuadradinhos(cores, letrasDigitadas, dicas, palavraSecreta, linhaChute);
                limparAbaixo();
                TocarSom.tocarSom("GTAGanhou.wav", false);

                mudarCor(AMARELO_GTA);

                System.out.println("            ╔╦╗╦╔═╗╔═╗╦╔═╗╔╗╔  ╔═╗╔═╗╔═╗╔═╗╔═╗╔╦╗");
                System.out.println("            ║║║║╚═╗╚═╗║║ ║║║║  ╠═╝╠═╣╚═╗╚═╗╠═  ║║");
                System.out.println("            ╩ ╩╩╚═╝╚═╝╩╚═╝╝╚╝  ╩  ╩ ╩╚═╝╚═╝╚═╝═╩╝");

                mudarCor(COR_PADRAO);

                System.out.println();
                System.out.println("                    ╦═╗╔═╗╔═╗╔═╗╔═╗╔═╗╔╦╗  ╔═╗══╗   ");
                System.out.println("                    ╠╦╝╠═ ╚═╗╠═╝╠═ ║   ║   ╠═╗  ║═╬═");
                System.out.println("                    ╩╚═╚═╝╚═╝╩  ╚═╝╚═╝ ╩   ╚═╝  ╩   ");

                System.out.println();
                System.out.println("PARABÉNS! Você acertou em " + (linhaChute + 1) + " tentativa(s)!");
                return true;
            }

            linhaChute++;
        }

        voltarAoTopo();
        System.out.println("             T E R M O ");
        System.out.println();
        imprimirQuadradinhos(cores, letrasDigitadas, dicas, palavraSecreta, linhaChute);
        limparAbaixo();
        TocarSom.tocarSom("GTAPerdeu.wav", false);

        mudarCor(VERMELHO);

        System.out.println("                   ╦ ╦╔═╗╔═╗╔╦╗╔═╗╔╦╗");
        System.out.println("                   ║║║╠═╣╚═╗ ║ ║╣  ║║");
        System.out.println("                   ╚╩╝╩ ╩╚═╝ ╩ ╚═╝═╩╝");

        mudarCor(COR_PADRAO);

        System.out.println();
        System.out.println("A palavra era: " + palavraSecreta.toUpperCase());
        return false;
    }

    public static void calcularProgressoRodadas() {

        int rodadas = 0;
        int vitorias = 0;
        int sequenciaVitorias = 0;
        int melhorSequencia = 0;

        while (true) {

            // so pra limpar o que ficou da rodada anterior e n imprimir outra matriz
            voltarAoTopo();
            limparAbaixo();

            String palavraSecreta = sortearPalavraSecreta();
            rodadas++;

            if (jogarRodada(palavraSecreta)) {
                vitorias++;
                sequenciaVitorias++;
                if (sequenciaVitorias > melhorSequencia) {
                    melhorSequencia = sequenciaVitorias;
                }
            } else {
                sequenciaVitorias = 0;
            }

            String simOuNao;

            System.out.println();
            System.out.println("Deseja jogar novamente? Digite 'S'/'s' para sim e 'N'/'n' para não!");

            do {
                simOuNao = minusculotirarAcento(lerTexto());

                if (!simOuNao.equals("s") && !simOuNao.equals("n")) {

                    System.out.println();
                    System.out.println("Dígito errado! Digite 'S'/'s' ou 'N'/'n'.");
                }
            } while (!simOuNao.equals("s") && !simOuNao.equals("n"));

            if (simOuNao.equals("n")) {

                double doublePorcentagem = vitorias / rodadas * 100.0;
                int porcentagem = (int) doublePorcentagem;

                System.out.println();
                mudarCor(AMARELO_GTA);
                System.out.println(" Agradecemos por jogar o nosso Termo!");
                mudarCor(COR_PADRAO);

                System.out.println();
                System.out.println("╔════════════════════════════════════╗");
                System.out.println("║            SEU PROGRESSO           ║");
                System.out.println("╠════════════════════════════════════╣");
                System.out.printf("║  Rodadas jogadas:            %4d  ║%n", rodadas);
                System.out.printf("║  Vitórias:                   %4d  ║%n", vitorias);
                System.out.printf("║  %% de vitórias:              %3d%%  ║%n", porcentagem);
                System.out.println("╟────────────────────────────────────╢");
                System.out.printf("║  Sequência atual:            %4d  ║%n", sequenciaVitorias);
                System.out.printf("║  Melhor sequência:           %4d  ║%n", melhorSequencia);
                System.out.println("╚════════════════════════════════════╝");
                TocarSom.tocarSom("FEIN.wav", true);
                return;
            }
        }

    }

}
