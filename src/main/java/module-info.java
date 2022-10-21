import javax.management.remote.JMXConnectorProvider;
import javax.management.remote.JMXConnectorServerProvider;

import com.sun.jmx.remote.protocol.jmxmp.ClientProvider;
import com.sun.jmx.remote.protocol.jmxmp.ServerProvider;

module druvu.jmx.optional {

	requires java.management;
	requires java.security.sasl;
	requires java.rmi;
	requires java.logging;

	provides JMXConnectorServerProvider with ServerProvider;
	provides JMXConnectorProvider with ClientProvider;
}
