package eu.alpinweiss.filegen.service;

import java.util.Set;

import eu.alpinweiss.filegen.config.FdrStep;
import eu.alpinweiss.filegen.service.impl.XmlConfigParserImpl;

import com.google.inject.ImplementedBy;

/**
 * {@link XmlConfigParser}.
 * 
 * @author Aleksandrs.Severgins | <a href="http://alpinweiss.eu">SIA Alpinweiss</a>
 * 
 */
@ImplementedBy(XmlConfigParserImpl.class)
public interface XmlConfigParser {

	Set<FdrStep> getFdrSteps(String fileName);
}
