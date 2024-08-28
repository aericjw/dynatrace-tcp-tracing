# dynatrace-tcp-tracing

### Description

This code repository serves as Java **reference code** to enable Distributed Tracing over pure TCP calls.

This can be a great workaround for legacy systems that do not use traditional application communication like HTTP/REST API, GraphQL, gRPC, etc.

Utilizing the OneAgent SDK will allow for systems that communicate with pure TCP to show up in service flows, backtrace, and distributed traces.

### Pre-reqs
To deploy and get started with this code, the following pre-reqs are needed
- SpringBoot, use this [pre-configured SpringBoot configuration](https://start.spring.io/#!type=maven-project&language=java&platformVersion=3.3.3&packaging=jar&jvmVersion=21&groupId=com.example&artifactId=demo&name=demo&description=Demo%20project%20for%20Spring%20Boot&packageName=com.example.demo&dependencies=web)
- Maven
- [OneAgentSDK for Java](https://github.com/Dynatrace/OneAgent-SDK-for-Java)
- [OneAgent installed in full-stack mode](https://docs.dynatrace.com/docs/shortlink/oneagent-installation-subsection) (for Kubernetes deployments you can use [Cloud Native Full Stack](https://docs.dynatrace.com/docs/shortlink/installation-k8s-cloud-native-fs), [Classic Full Stack](https://docs.dynatrace.com/docs/shortlink/installation-k8s-classic-fs), or [Application Only](https://docs.dynatrace.com/docs/shortlink/installation-k8s-automated-app-monitoring) deployments)

### Support

This project is not supported by Dynatrace, and is only to be used as a **reference** to enable TCP tracing in your own systems. There should be no expectation to receive support or that code can be copy-pasted into your source code and being to work.