/*
 * DOMTranslatorResult.java
 */

package org.translet.processor.dom;

import org.w3c.dom.Document;
import org.translet.processor.TranslatorResult;

/**
 * Acts as a holder of the translated result tree, in
 * the form of a Document Object Model (dom) tree. The
 * there is no output DOM source set, then a new
 * Document node will be created that will hold
 * the result of the translation.
 */
public class DOMTranslatorResult implements TranslatorResult
{
    private Document doc;

    /**
     * Zero-argument default constructor.
     */
    public DOMTranslatorResult() { this(null); }
    /**
     * Uses the DOM document for creating the
     * resulting xml nodes. All xpaths are relative
     * to the root of this document.
     *
     * @param doc DOM document to which the resulting
     * elements will be appended.
     */
    public DOMTranslatorResult(Document doc) { this.doc = doc; }

    /**
     * Sets the document that will hold the results of
     * the translation.
     *
     * @param doc DOM document to which the resulting
     * elements will be appended.
     */
    public void setDocument(Document doc) { this.doc = doc; }

    /**
     * Returns the DOM document that holds the results
     * of the translation.
     *
     * @return DOM document that holds the results
     */
    public Document getDocument() { return this.doc; }
}