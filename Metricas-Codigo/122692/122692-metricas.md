# 📈 Análise de Métricas – Requisito "forex / moedas"

## Classe: CurrencyService

### Comentário
Classe de serviço simples e coesa, responsável por obter e converter as taxas de câmbio.
As métricas esperadas (CBO moderado, LCOM baixo, RFC pequeno) indicam boa coesão e acoplamento controlado.
O método principal (`getRates`) apresenta complexidade média (CCC ≈ 7–10) devido a várias verificações e parsing JSON.
Há acoplamento direto a bibliotecas externas (`OkHttpClient`, `ObjectMapper`) e valores fixos (`API_URL`, `SYMBOLS`),
o que reduz flexibilidade e testabilidade. A métrica ATFD é relativamente alta, pois manipula dados externos.

### Ações sugeridas
- Injetar `baseUrl` e `symbols` através de `application.properties` ou `@ConfigurationProperties`.
- Extrair interface `RatesClient` para isolar dependências externas e facilitar testes.
- Dividir lógica: `buildRequest()`, `parseRates(JsonNode)` (→ reduzir complexidade).
- Adicionar logs estruturados (SLF4J) em vez de lançar apenas `IOException`.
- Implementar cache curto (ex.: 60 s) para evitar chamadas repetidas à API.
- Converter erros genéricos em `RatesException` (exceção de domínio).

---

## Classe: CurrencyView

### Comentário
Camada de interface Vaadin limpa e legível.
As métricas indicam baixa complexidade e bom acoplamento (CBO baixo).
Boas práticas: utilização de `Notification` para feedback e `Grid` configurado corretamente.
Pontos menores: falta formatação da taxa de câmbio segundo `Locale` e ausência de ordenação inicial.
A `Notification` mostra mensagens técnicas (“Erro a obter câmbios: …”) em vez de mensagens de utilizador.

### Ações sugeridas
- Formatar taxa com `NumberFormat.getNumberInstance(getLocale())`.
- Adicionar ordenação por “Moeda” por defeito.
- Usar mensagens i18n com `getTranslation()` e cabeçalhos localizados.
- Adicionar logs de erro no servidor e notificação amigável no cliente.
- (Opcional) adicionar auto-refresh periódico da grelha.

---

## Método: CurrencyService.getRates()

### Comentário
Método responsável por efetuar pedido HTTP, validar resposta e extrair “rates”.
Complexidade ciclomatica média (5–9).  
Camadas de verificação (`res.isSuccessful()`, `body != null`, presença de “rates”) bem estruturadas.
Dependência direta de OkHttp/Jackson aumenta CBO; método mistura responsabilidades (rede + parsing + negócio).

### Ações sugeridas
- Extrair métodos auxiliares: `buildRequest()`, `parseRates(JsonNode)`.
- Isolar dependência HTTP num cliente injetável (`RatesClient`).
- Propagar exceções como `RatesException` em vez de `IOException`.
- Logar resultados e erros com SLF4J.

---

## Método: CurrencyView.loadData()

### Comentário
Método simples de atualização da grelha.  
Baixa complexidade (CC ≈ 3), profundidade de aninhamento mínima.  
Trata `IOException` e apresenta notificação ao utilizador.  
Boa separação entre lógica de UI e serviço, mas ausência de formatação da taxa e ordenação dos dados.

### Ações sugeridas
- Formatar valores numéricos de forma local (`NumberFormat`).
- Adicionar `Comparator` para ordenar por código da moeda.
- Mostrar mensagem amigável no caso de falha (sem detalhes técnicos).
- Logar exceção a nível de servidor.

---

## Resumo de Refatoração Geral

- Criar `ForexProperties` com `@ConfigurationProperties(prefix="forex")` contendo `baseUrl` e `symbols`.
- Introduzir interface `RatesClient` e implementação `FrankfurterClient` separando rede de negócio.
- Converter exceções genéricas em domínio (`RatesException`).
- Adicionar testes unitários e de integração (WireMock ou MockWebServer).
- Melhorar UX e acessibilidade (i18n, formatação, ordenação).
- Garantir logging estruturado e mensagens de utilizador claras.
