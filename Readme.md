# Simulação da corrupção

Esse trabalho é baseado nesse [TCC](http://bdm.unb.br/bitstream/10483/8419/1/2014_MatheusSchmelingCosta.pdf)

# Funcionamento do algoritmo de Hammond

Cada agente possui a predisposição de agir de forma corrupta ou não, caso um agente corrupto tiver um vizinho não corrupto, o agente corrupto é denunciado. Após certo número de denuncia o agente será preso. Os agentes entretanto não sabem quantas vezes foram denunciados ou quantas vezes podem praticar corrupção sem serem presos. Isso faz com que não saibam a probabilidade real de serem capturados, aumentando gradualmente sua taxa de corrupção.

*Para formular sua decisão, primeiramente, o agente verifica a probabilidade de se deparar com um agente corrupto a partir de suas experiências passadas. Dada uma memória de tamanho N, A = n / N. Onde A é sua expectativa de encontrar um agente corrupto e n o número de vezes que encontrou um agente corrupto no passado. A percepção de sua probabilidade de ser capturado é dada por B=m/M, onde m é a quantidade de agentes de sua vizinhança que foram presos e M é a quantidade de agentes que atuaram de forma corrupta.* (COSTA, 2014)

# Como executar

Basta executar em dois terminais diferentes os seguintes comandos
```
mvn -Pjade-main exec:java
mvn -Pjade-agent exec:java
```

# Referências

http://www.cs.sjsu.edu/~pearce/modules/lectures/eco2/ca/corruption.htm
