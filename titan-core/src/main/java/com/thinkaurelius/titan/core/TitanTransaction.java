
package com.thinkaurelius.titan.core;

import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.thinkaurelius.titan.graphdb.query.ElementQueryBuilder;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.KeyIndexableGraph;
import com.tinkerpop.blueprints.TransactionalGraph;
import com.tinkerpop.blueprints.Vertex;

/**
 * TitanTransaction defines a transactional context for a {@link TitanGraph}. Since TitanGraph is a transactional graph
 * database, all interactions with the graph are mitigated by a TitanTransaction.
 * <p/>
 * All vertex and edge retrievals are channeled by a graph transaction which bundles all such retrievals, creations and
 * deletions into one transaction. A graph transaction is analogous to a
 * <a href="http://en.wikipedia.org/wiki/Database_transaction">database transaction</a>.
 * The isolation level and <a href="http://en.wikipedia.org/wiki/ACID">ACID support</a> are configured through the storage
 * backend, meaning whatever level of isolation is supported by the storage backend is mirrored by a graph transaction.
 * <p/>
 * A graph transaction supports:
 * <ul>
 * <li>Creating vertices, properties and edges</li>
 * <li>Creating types</li>
 * <li>Index-based retrieval of vertices</li>
 * <li>Querying edges and vertices</li>
 * <li>Aborting and committing transaction</li>
 * </ul>
 *
 * @author Matthias Br&ouml;cheler (http://www.matthiasb.com)
 */
public interface TitanTransaction extends TransactionalGraph, KeyIndexableGraph {

    /**
     * Creates a new vertex in the graph.
     *
     * @return New vertex in the graph created in the context of this transaction.
     */
    public TitanVertex addVertex();

    /**
     * Creates a new edge connecting the specified vertices.
     * <p/>
     * Creates and returns a new {@link TitanEdge} with given label connecting the vertices in the order
     * specified.
     *
     * @param label     label of the edge to be created
     * @param outVertex outgoing vertex of the edge
     * @param inVertex  incoming vertex of the edge
     * @return new edge
     */
    public TitanEdge addEdge(TitanVertex outVertex, TitanVertex inVertex, TitanLabel label);

    /**
     * Creates a new edge connecting the specified vertices.
     * <p/>
     * Creates and returns a new {@link TitanEdge} with given label connecting the vertices in the order
     * specified.
     * <br />
     * Automatically creates the edge label if it does not exist and automatic creation of types is enabled. Otherwise,
     * this method with throw an {@link IllegalArgumentException}.
     *
     * @param label     label of the edge to be created
     * @param outVertex outgoing vertex of the edge
     * @param inVertex  incoming vertex of the edge
     * @return new edge
     */
    public TitanEdge addEdge(TitanVertex outVertex, TitanVertex inVertex, String label);

    /**
     * Creates a new property for the given vertex and key with the specified attribute.
     * <p/>
     * Creates and returns a new {@link TitanProperty} with specified property key and the given object being the attribute.
     *
     * @param key       key of the property to be created
     * @param vertex    vertex for which to create the property
     * @param attribute attribute of the property to be created
     * @return new property
     * @throws IllegalArgumentException if the attribute does not match the data type of the given property key.
     */
    public TitanProperty addProperty(TitanVertex vertex, TitanKey key, Object attribute);

    /**
     * Creates a new property for the given vertex and key with the specified attribute.
     * <p/>
     * Creates and returns a new {@link TitanProperty} with specified property key and the given object being the attribute.
     * <br />
     * Automatically creates the property key if it does not exist and automatic creation of types is enabled. Otherwise,
     * this method with throw an {@link IllegalArgumentException}.
     *
     * @param key       key of the property to be created
     * @param vertex    vertex for which to create the property
     * @param attribute attribute of the property to be created
     * @return new property
     * @throws IllegalArgumentException if the attribute does not match the data type of the given property key.
     */
    public TitanProperty addProperty(TitanVertex vertex, String key, Object attribute);

    /**
     * Retrieves the vertex for the specified id.
     *
     * @param id id of the vertex to retrieve
     * @return vertex with the given id if it exists, else null
     * @see #containsVertex
     */
    public TitanVertex getVertex(long id);

    /**
     * Checks whether a vertex with the specified id exists in the graph database.
     *
     * @param vertexid vertex id
     * @return true, if a vertex with that id exists, else false
     */
    public boolean containsVertex(long vertexid);

    public ElementQuery query();

    public TitanVertex getVertex(TitanKey key, Object attribute);

    public TitanVertex getVertex(String key, Object attribute);

    /**
     * Retrieves all vertices which have an incident property of the given key with the specified value.
     * <p/>
     * The given property key must be indexed. In this regard, it violates the Blueprints contract which
     * requires iterating over all vertices and filtering based on the attribute. However, Titan does not
     * support vertex iteration.
     *
     * @param key       property key
     * @param attribute attribute value
     * @return All vertices which have an incident property of the given key with the specified value.
     * @throws IllegalArgumentException if the property key is not indexed.
     * @see com.thinkaurelius.titan.core.TitanKey#hasIndex()
     */
    public Iterable<TitanVertex> getVertices(TitanKey key, Object attribute);

    /**
     * Retrieves all vertices which have an incident property of the given key with the specified value.
     * <p/>
     * The given property key must be indexed. In this regard, it violates the Blueprints contract which
     * requires iterating over all vertices and filtering based on the attribute. However, Titan does not
     * support vertex iteration.
     *
     * @param key       property key
     * @param attribute attribute value
     * @return All vertices which have an incident property of the given key with the specified value.
     * @throws IllegalArgumentException if the property key is not indexed.
     * @see com.thinkaurelius.titan.core.TitanKey#hasIndex()
     */
    public Iterable<Vertex> getVertices(String key, Object attribute);

    public Iterable<TitanEdge> getEdges(TitanKey key, Object attribute);

    /**
     * Checks whether a type with the specified name exists.
     *
     * @param name name of the type
     * @return true, if a type with the given name exists, else false
     */
    public boolean containsType(String name);

    /**
     * Returns the type with the given name.
     * Note, that type names must be unique.
     *
     * @param name name of the type to return
     * @return The type with the given name, or null if such does not exist
     * @see TitanType
     */
    public TitanType getType(String name);

    /**
     * Returns the property key with the given name.
     *
     * @param name name of the property key to return
     * @return the property key with the given name
     * @throws IllegalArgumentException if a property key with the given name does not exist or if the
     *                                  type with the given name is not a property key
     * @see TitanKey
     */
    public TitanKey getPropertyKey(String name);

    /**
     * Returns the edge label with the given name.
     *
     * @param name name of the edge label to return
     * @return the edge label with the given name
     * @throws IllegalArgumentException if an edge label with the given name does not exist or if the
     *                                  type with the given name is not an edge label
     * @see TitanLabel
     */
    public TitanLabel getEdgeLabel(String name);

    /**
     * Returns a new {@link TypeMaker} instance to create types.
     * <p/>
     * The type constructed with this maker will be created in the context of this transaction.
     *
     * @return a type maker linked to this transaction.
     * @see TypeMaker
     * @see TitanType
     */
    public TypeMaker makeType();

    /**
     * Commits and closes the transaction.
     * <p/>
     * Will attempt to persist all modifications which may result in exceptions in case of persistence failures or
     * lock contention.
     * <br />
     * The call releases data structures if possible. All element references (e.g. vertex objects) retrieved
     * through this transaction are stale after the transaction closes and should no longer be used.
     *
     * @throws com.thinkaurelius.titan.diskstorage.StorageException
     *          if an error arises during persistence
     */
    public void commit();

    /**
     * Aborts and closes the transaction. Will discard all modifications.
     * <p/>
     * The call releases data structures if possible. All element references (e.g. vertex objects) retrieved
     * through this transaction are stale after the transaction closes and should no longer be used.
     *
     * @throws com.thinkaurelius.titan.diskstorage.StorageException
     *          if an error arises when releasing the transaction handle
     */
    public void rollback();

    /**
     * Checks whether the transaction is still open.
     *
     * @return true, when the transaction is open, else false
     */
    public boolean isOpen();

    /**
     * Checks whether the transaction has been closed.
     *
     * @return true, if the transaction has been closed, else false
     */
    public boolean isClosed();

    /**
     * Checks whether any changes to the graph database have been made in this transaction.
     * <p/>
     * A modification may be an edge or vertex update, addition, or deletion.
     *
     * @return true, if the transaction contains updates, else false.
     */
    public boolean hasModifications();

}
