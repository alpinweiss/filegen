package eu.alpinweiss.filegen.service;

import eu.alpinweiss.filegen.config.FdrStep;

import java.util.Set;

/**
 * {@link XmlConfigParser}.
 * 
 * @author Aleksandrs.Severgins | <a href="http://alpinweiss.eu">SIA Alpinweiss</a>
 * 
 */
public interface XmlConfigParser {

	Set<FdrStep> getFdrSteps(String fileName);
}
