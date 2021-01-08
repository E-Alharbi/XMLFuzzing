package org.translet.processor;

import java.util.Map;
import java.util.HashMap;

import javax.xml.transform.TransformerException;

import org.apache.xpath.*;

import org.apache.xpath.compiler.Compiler;
import org.apache.xpath.compiler.XPathParser;

import org.translet.helper.Logger;

/**
 * Default implementation of the <tt>XPathProcessor</tt>.
 *
 * @author Karthikeyan Mariappan.
 */
public class XPathProcessorImpl implements XPathProcessor
{
	private static final Logger log =
		Logger.getInstance(XPathProcessorImpl.class.getName());

	private static Map operatorsTable;

	private LocationPathHandler handler;

	static
	{
		operatorsTable = new HashMap(12);
		operatorsTable.put("|",null);
		operatorsTable.put("+",null);
		//operatorsTable.put("/",null);
		operatorsTable.put("-",null);
		operatorsTable.put("=",null);
		operatorsTable.put("!",null);
		operatorsTable.put("<",null);
		operatorsTable.put(">",null);
		operatorsTable.put("and",null);
		operatorsTable.put("or",null);
		operatorsTable.put("mod",null);
		operatorsTable.put("div",null);
	}
	//private Compiler    compiler;
	//private XPathParser parser;

	public XPathProcessorImpl(String name)
	{
	}

	public void processXPath(XPathExpr xpath)
	throws ProcessException
	{
		try
		{
			// FIXME. Include errorhandler and prefix resolvers.
			//if(null==compiler)
			Compiler compiler  = new Compiler();

            if(null==handler)
                throw new IllegalStateException("The LocationpathHandler is null");

			//if(null==parser)
			XPathParser parser = new XPathParser(null,null);

            parser.initXPath(compiler,xpath.getExpression(),null);

			TokenIterator iter = new TokenIteratorImpl(compiler.getTokenQueue(),
												0, compiler.getTokenQueueSize());

			if(log.isDebugEnabled())
            {
                printTokens(iter);
                iter.rewind();
            }

			handler.startXPath();
			step(iter);
			handler.data(xpath.getData());
			handler.endXPath();
		}
		catch(TransformerException tre)
		{
			throw new ProcessException(tre);
		}
	}

	private void step(TokenIterator iter) throws TransformerException
	{
		if(null==handler)
			throw new IllegalStateException("Handler is not initialized.");


		while(iter.hasNext())
		{
			if(iter.nextTokenIs('/'))
			{
				// nothing interesting.
				iter.next();
			}
			else if(iter.nextTokenIs('['))
			{
				PredicateExpr pr = parsePredicate(iter);
				log.debug("[tostring] Predicate: "+pr);
				handler.predicate(pr);
			}
			else if(iter.nextTokenIs('@'))
			{
				iter.next();
                String attrName = iter.next();
                // check if attrName is just a prefix.
                if(iter.nextTokenIs(':'))
                    attrName = attrName + iter.next() + iter.next();

                handler.attribute(attrName);
			}
			else
			{
                String tagName = iter.next();
                // check if tagName is just a prefix.
                if(iter.nextTokenIs(':'))
                    tagName = tagName + iter.next() + iter.next();

                handler.locationStep(tagName);
			}
		};

	}

	public void setHandler(LocationPathHandler handler)
	{
		this.handler = handler;
	}

	private void printTokens(TokenIterator iter)
	{
		for(;iter.hasNext();log.debug(iter.next()));
	}

	private PredicateExpr parsePredicate(TokenIterator iter)
	{
		PredicateExpr left  = null;
		PredicateExpr right = null;

		PredicateExpr cpe   = null;
		String operator     = null;

		// to get rid of '['
		iter.next();
		iter.markForSnapshot();

		while(iter.hasNext())
		{
			String token = iter.peekNext();

			log.debug("[createPredicate] token: "+token);
			log.debug("[createPredicate] left:"+left+", right:"+right);

			if(iter.nextTokenIs(']'))
			{
				iter.next();
				log.debug("[createPredicate] end of predicate");

				// Complete the compound predicate expr.
				if(null==left)
				{
					log.debug("[createPredicate] Final simple predicate");
                    cpe = new SimplePredicateExpr(iter.createSnapshot(),handler);
				}
				else if(null==right)
				{
					log.debug("[createPredicate] Final right predicate");
                    right = new SimplePredicateExpr(iter.createSnapshot(),handler);
					cpe = new CompoundPredicateExpr(left,operator,right);
				}
				else
					cpe = new CompoundPredicateExpr(left,operator,right);

				break;
			}
			else if(!isOperator(token))
			{
				log.debug("[createPredicate] not operator.");

				iter.next();
				continue;
			}

			log.debug("[createPredicate] Operator.");

			// This is an operator and hence process the expressions.

			if(null==left)
			{
				log.debug("[createPredicate] Creating LEFT expr.");
                left = new SimplePredicateExpr(iter.createSnapshot(),handler);
			}
			else if(null==right)
			{
				log.debug("[createPredicate] Creating RIGHT expr.");
                right = new SimplePredicateExpr(iter.createSnapshot(),handler);
			}
			else
			{
				log.debug("[createPredicate] Creating COMPD expr.");
				left  = new CompoundPredicateExpr(left,operator,right);
				right = null;
				// Needed to reset the snapshot
				iter.createSnapshot();
			}

			// Each simple expression needs the iterator to
			// iterator over the token for which it is responsible.
			// So, once an operator token is found, create a snapshot
			// until the next operator.
			operator = token;
			iter.next();
			String nextToken = iter.peekNext();
			if(isOperator(nextToken))
			{
				operator += nextToken;
				iter.next();
			}

			iter.markForSnapshot();
		};

		return cpe;
	}

	private boolean isOperator(String token)
	{
		return operatorsTable.containsKey(token);
	}

    public LocationPathHandler getHandler() { return this.handler; }
} // Class End.
