package cluster;

import graph.Edge;
import graph.Node;
import org.junit.Before;
import org.junit.Test;
import switches.Switch;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class ClusterTest {

    private Cluster<Node, Cable> cluster;

    @Before
    public void setup() {
        this.cluster = ClusterFactory.simpleEmptyCluster(3);
    }

    @Test
    public void getServers() {
        assertEquals(4, this.cluster.getNodes().size());
        assertEquals(3, this.cluster.getServers().size());
    }

    @Test
    public void testGetConnection() {
        Server server1 = this.cluster.getServers().get(0);
        Server server2 = this.cluster.getServers().get(1);

        Switch switch1 = (Switch) this.cluster.getById(1);

        assertNotNull(server1);
        assertNotNull(server2);
        assertNotNull(switch1);
        assertNotEquals(server1, server2);

        Connection connectionServerToSwitch = this.cluster.getConnection(Connection.Type.INTERNAL, server1, switch1);
        this.assertCorrectConnection(connectionServerToSwitch, server1, switch1, 1);

        Connection connectionServerToServer = this.cluster.getConnection(Connection.Type.INTERNAL, server1, server2);
        this.assertCorrectConnection(connectionServerToServer, server1, server2, 2);

        Edge cable1 = connectionServerToServer.getEdges().get(0);
        Edge cable2 = connectionServerToServer.getEdges().get(1);
        Edge cableServer1, cableServer2;
        if (cable1.getFirstNode().equals(server1) || cable1.getSecondNode().equals(server1)) {
            cableServer1 = cable1;
            cableServer2 = cable2;
        } else {
            cableServer1 = cable2;
            cableServer2 = cable1;
        }

        assertTrue(
                (cableServer1.getFirstNode().equals(server1) && cableServer1.getSecondNode().equals(switch1)) ^
                (cableServer1.getFirstNode().equals(switch1) && cableServer1.getSecondNode().equals(server1))
        );

        assertTrue(
                (cableServer2.getFirstNode().equals(server2) && cableServer2.getSecondNode().equals(switch1)) ^
                (cableServer2.getFirstNode().equals(switch1) && cableServer2.getSecondNode().equals(server2))
        );
    }

    private void assertCorrectConnection(Connection connection, Node node1, Node node2, int connectionLength) {
        assertNotNull(connection.getFirstEndPoint());
        assertNotNull(connection.getSecondEndPoint());

        assertNotEquals("The endpoints should be different", connection.getFirstEndPoint(), connection.getSecondEndPoint());

        assertTrue(
                "The endpoints of the connection should be correct: " + connection,
                (connection.getFirstEndPoint().equals(node1) && connection.getSecondEndPoint().equals(node2)) ^
                (connection.getFirstEndPoint().equals(node2) && connection.getSecondEndPoint().equals(node1))
        );

        assertEquals(
                "There should be a connection with length " + connectionLength + " from " + node1 + " to " + node2,
                connectionLength, connection.getEdges().size()
        );
    }

}
