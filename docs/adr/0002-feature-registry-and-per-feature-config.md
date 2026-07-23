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
- **Autoridade por natureza da feature:**
  - Features de gameplay (receitas, worldgen, encantamentos, interações de bloco, drops) são **server-authoritative** e, sempre que possível, não exigem o mod no cliente em multiplayer.
  - Features puramente visuais/UX são **client-side**.

## Rationale

- Toggle por feature é o padrão esperado de um mod agregador; permite ao usuário curar sua experiência.
- Cloth Config + ModMenu é o caminho idiomático no Fabric e já é parcialmente adotado por um dos refs.
- Separar autoridade por natureza preserva compatibilidade em servidores (features de gameplay não forçam mod no cliente).

## Consequences

- Cada feature de **comportamento** (eventos, encantamentos, mixins) checa sua flag no ponto de entrada.
- **Granularidade do toggle (resolvido):** só features de comportamento são toggleáveis. **Receitas e worldgen são sempre ligados** (datagen'd e empacotados; sem filtro em runtime). Evita gancho no `RecipeManager` e complexidade de remoção condicional. Trade-off aceito: perde-se toggle por-receita.
- Exceções ao toggle (always-on por natureza): worldgen do better-trees (ver `docs/FEATURES.md`) e todo conteúdo de receita.
