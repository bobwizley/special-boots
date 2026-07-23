---
number: 006
title: Representação vanilla-compatible para Souls
date: 2026-07-22
author: Victor Ferreira
status: accepted
---

## Context

Soul Links precisa exibir no mundo um recipiente persistente e interativo para itens e experiência. Registrar um tipo de entidade customizado acoplaria o protocolo e o registry do cliente ao RootBoot, podendo impedir clientes sem o mod de entrar no servidor. O projeto promete que features de gameplay funcionem sem mod no cliente sempre que possível.

## Decision

Uma Soul será representada no mundo somente por tipos de entidade que clientes vanilla conhecem. Propriedade, inventário, experiência e ciclo de vida permanecem autoritativos e persistidos pelo servidor. Soul Links não registra um tipo de entidade customizado nem exige protocolo customizado do cliente.

## Rationale

Entidades vanilla oferecem representação visual suficiente para o comportamento observável da Soul, enquanto o servidor pode controlar proximidade, propriedade e recuperação. Essa separação preserva clientes sem RootBoot sem abrir mão da autoridade ou persistência necessárias.

## Considered Alternatives

- **Entidade customizada:** rejeitada porque tornaria uma feature de gameplay dependente do mod no cliente e contrariaria a meta explícita de compatibilidade multiplayer.
