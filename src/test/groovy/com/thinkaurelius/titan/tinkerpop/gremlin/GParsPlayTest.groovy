package com.thinkaurelius.titan.tinkerpop.gremlin

import com.thinkaurelius.titan.core.TitanFactory
import com.thinkaurelius.titan.core.TitanGraph
import com.tinkerpop.blueprints.TransactionalGraph
import com.tinkerpop.blueprints.Vertex
import com.tinkerpop.blueprints.util.io.graphml.GraphMLReader
import com.tinkerpop.gremlin.groovy.Gremlin
import groovyx.gpars.GParsPool
import junit.framework.TestCase
import static com.tinkerpop.blueprints.TransactionalGraph.Conclusion.SUCCESS

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class GParsPlayTest extends TestCase {

    static {
        Gremlin.load();
    }

    public testGPars() {
        TitanGraph g = TitanFactory.open('target/testme');
        g.createKeyIndex("name", Vertex.class);
        g.stopTransaction(SUCCESS);
        new GraphMLReader(g).inputGraph(GraphMLReader.class.getResourceAsStream("graph-example-2.xml"));
        TransactionalGraph h = g.startThreadTransaction();
        def v = h.V('name', 'DARK STAR').next();

        //def l;
        GParsPool.withPool(10) {
            println v.name;
            def list = [];
            def result = v.out.eachParallel{it.out.name.fill(list)}
            println list;

        }
        //System.out.println(l);


        h.stopTransaction(SUCCESS);
        g.shutdown();

    }
}
