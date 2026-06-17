## Limitaciones del entorno AWS Academy

Se intentó implementar servicios adicionales administrados
(RDS y AWS Lambda), pero la cuenta de laboratorio poseía
restricciones IAM que impedían:

- rds:CreateDBInstance
- iam:CreateRole
- iam:PassRole

Por esta razón se implementó PostgreSQL dentro del clúster
Docker Swarm y se entrega el código fuente completo de la
función FaaS para su despliegue en un entorno AWS sin
restricciones.