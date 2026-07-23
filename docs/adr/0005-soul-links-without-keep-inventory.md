---
number: 005
title: Soul Links sem controlar keepInventory
date: 2026-07-22
author: Victor Ferreira
status: accepted
---

## Context

No Vanilla Refresh, Soul Links exige `keepInventory=true`, oferece ativar essa gamerule e usa o inventário preservado para formar a Soul. Essa estratégia altera uma regra global do mundo e pode surpreender administradores ou interferir com outros mods. O RootBoot precisa definir se preserva essa mecânica interna da referência ou se integra a Soul ao resultado normal da morte.

## Decision

Soul Links captura somente os itens e a experiência que a morte vanilla efetivamente descartaria. Quando `keepInventory=true`, nenhuma Soul é criada e a feature não altera itens nem XP. O RootBoot nunca ativa nem desativa `keepInventory` para viabilizar Soul Links.

Cada morte com alguma perda cria uma Soul independente e persistente. Um jogador pode manter várias Souls simultaneamente; uma nova morte não substitui, funde nem destrói as anteriores.

## Rationale

A gamerule pertence ao mundo e não deve ser controlada implicitamente por uma feature habilitada por padrão. Observar as perdas efetivas mantém Soul Links compatível com a intenção explícita do administrador, evita uma mudança global e reduz conflitos com outros mods que participem do fluxo de morte. A divergência da referência é deliberada.

## Considered Alternatives

- **Reproduzir o Vanilla Refresh:** rejeitado porque faria Soul Links depender de uma gamerule global e exigiria alterá-la ou impedir a feature de funcionar em sua configuração vanilla.
