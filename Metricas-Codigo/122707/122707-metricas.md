 ##                                      Análise de Qualidade e Métricas (Miguel Nunes-122707)

## Classes analisadas 
com.example.examplefeature.pdf.PdfService
com.example.examplefeature.pdf.PdfView

## Ferramentas utilizadas
IntelliJ e MetricsTree 


## Métricas (antes da refatoração)

### Classe PdfService

| Métrica | Valor | Interpretação |

| **LOC** (Linhas de código) | **73** | Tamanho médio. |
| **WMC** (Complexidade total da classe) | **13** | Complexidade relativamente alta → manutenção mais difícil. |
| **CBO** (Acoplamento) | **3** | Acoplamento baixo/aceitável. |
| **TCC** (Coesão apertada) | **0.1667** | Coesão fraca → métodos pouco relacionados. |
| **RFC** (Respostas possíveis da classe) | **33** | Indica esforço cognitivo moderado/alto para testar/manter. |

## Comentário   
A classe cumpre o objetivo (gerar PDF) mas concentra demasiada lógica no método `render(List<Task>)`: criação e configuração do `Document`, cabeçalho, tabela, linhas, formatação de datas, tratamento de vazio e erro. Mistura de responsabilidades e `try/catch (Exception)` genérico elevam a complexidade e reduzem a legibilidade. O helper `safe(...)` evita NPEs mas torna a leitura menos direta.

## Ações sugeridas (refactoring)
- Extract Method em `render(...)` para funções como `addTitle(...)`, `buildHeaderTable(...)`, `fillRows(...)`, `createDoc(...)`, `toBytes(...)`.
- Extract Constant: `DateTimeFormatter` como `private static final`.
- Error handling: capturar exceções específicas e **logar**; manter `renderError(...)` para feedback ao utilizador.
- Clarificar null-safety (usar `Objects.toString(x, "")` / `Optional` em vez do `safe(...)` onde fizer sentido).

---

### Classe PdfView

| Métrica | Valor | Interpretação |

| **LOC** | **24** | Classe pequena. |
| **WMC** | **3** | Complexidade baixa e controlada. |
| **CBO** | **1** | Excelente isolamento. |
| **TCC** | **0.0** | Típico em UI Vaadin (métodos orientados a eventos). |
| **RFC** | **19** | Interação moderada com a API Vaadin. |

## Comentário   
Código limpo e direto para a UI. O método `gerarPdf()` monta a URL, configura o `Anchor` e dispara o download. Falta apenas robustez/UX: tratamento de erro visual, nome do ficheiro e desativação do botão durante a geração.

**Ações sugeridas (refactoring/UX):**
- Renomear componentes para legibilidade (`gerar` → `gerarPdfButton`).
- Definir `download="tarefas-YYYYMMDD.pdf"` no `Anchor`.
- Desativar/ativar o botão durante a geração; `Notification` em caso de erro.
- (Opcional) i18n dos textos (“Gerar PDF”, “Descarregar PDF”, “PDF gerado.”).

---

## Sumário comparativo (antes)

| Classe | LOC | WMC | CBO | TCC | Observação |
|---|---:|---:|---:|---:|---|
| **PdfService** | **73** | **13** | **3** | **0.1667** | Complexidade e coesão a melhorar (método `render` muito denso). |
| **PdfView** | **24** | **3** | **1** | **0.0** | Simples e funcional; melhorias pontuais de UX/robustez. |

