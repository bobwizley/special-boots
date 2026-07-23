---
number: 002
title: Feature registry com config por feature (Cloth Config + ModMenu)
date: 2026-07-22
author: Victor Ferreira
status: accepted
---

## Context

O mod reúne dezenas de funcionalidades vindas de 10 refs distintos. Sem um mecanismo de toggle, o usuário teria que aceitar tudo ou nada. Os próprios refs sinalizam a necessidade: o Vanilla-Refresh tem um menu de settings próprio e o `special-boots` já usa ModMenu.

## Decision

Toda funcionalidade é registrada num **feature registry** interno: cada feature tem um id estável, e nasce consultando uma flag de config que a liga/desliga independentemente das demais.

- **Biblioteca de config:** Cloth Config (persistência + tela) integrada ao ModMenu.
- **Arquivo único** de config para o mod inteiro.
- **Valor padrão:** todas as features de comportamento incluídas são habilitadas por padrão.
- **Ciclo de aplicação:** flags são consultadas durante o registro dos handlers; mudanças feitas na tela de configuração entram em vigor somente após reiniciar o jogo ou servidor. A interface informa essa exigência.
- **Autoridade por natureza da feature:**
  - Features de gameplay (receitas, worldgen, encantamentos, interações de bloco, drops) são **server-authoritative** e, sempre que possível, não exigem o mod no cliente em multiplayer.
  - Features puramente visuais/UX são **client-side**.
  - Em multiplayer, a configuração de gameplay do servidor é autoritativa.
  - Features server-authoritative evitam tipos e protocolos customizados quando uma representação vanilla consegue preservar o comportamento, permitindo clientes sem RootBoot. Soul Links segue essa regra explicitamente (ADR-0006).
  - Heavyfoot e Lightfoot são exceções: por serem encantamentos customizados, seu uso e apresentação corretos exigem RootBoot no cliente.

## Rationale

- Toggle por feature é o padrão esperado de um mod agregador; permite ao usuário curar sua experiência.
- Cloth Config + ModMenu é o caminho idiomático no Fabric e já é parcialmente adotado por um dos refs.
- Separar autoridade por natureza preserva compatibilidade em servidores (features de gameplay não forçam mod no cliente).

## Consequences

- Cada feature de **comportamento** (eventos, encantamentos, mixins) checa sua flag no ponto de entrada.
- **Granularidade do toggle (resolvido):** só features de comportamento são toggleáveis. **Receitas e worldgen são sempre ligados** (datagen'd e empacotados; sem filtro em runtime). Evita gancho no `RecipeManager` e complexidade de remoção condicional. Trade-off aceito: perde-se toggle por-receita.
- Exceções ao toggle (always-on por natureza): worldgen do better-trees (ver `docs/FEATURES.md`) e todo conteúdo de receita.
- Features habilitadas respeitam regras vanilla que eliminem seu gatilho. Em particular, Soul Links só captura perdas efetivas da morte e não atua quando `keepInventory=true`.
- **Exceção de persistência:** o tipo e a recuperação de Souls permanecem registrados mesmo com Soul Links desabilitada. O toggle impede somente novas Souls; as existentes continuam protegidas e recuperáveis para não aprisionar ou perder conteúdo persistido.
- Ao desabilitar Death items don't despawn, itens já protegidos recebem um novo prazo vanilla completo de despawn; novos drops não recebem proteção.
- Itens excedentes liberados por uma Soul recuperada recebem ou não proteção contra despawn conforme o estado de Death items don't despawn.
- **Exceção de vínculo persistente:** desabilitar Echo Shard Silence impede somente novos vínculos. Vínculos existentes permanecem ativos e continuam aceitando remoção por água ou devolvendo o shard na morte.
- **Exceção de display persistente:** desabilitar Stands & Frames Invisibility impede novas aplicações, mas mantém a reversão por água para displays já ocultos pelo RootBoot.
- **Reversão de atributo persistente:** ao desabilitar Half-health Babies, bebês existentes recuperam a vida máxima efetiva que teriam sem a redução multiplicativa do RootBoot quando carregados, preservando a proporção de vida atual.
