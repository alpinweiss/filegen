package eu.alpinweiss.filegen.service.impl;

import com.google.inject.Singleton;
import eu.alpinweiss.filegen.config.FdrStep;
import eu.alpinweiss.filegen.service.XmlConfigParser;
import org.w3c.dom.CharacterData;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

/**
 * {@link XmlConfigParserImpl}.
 * 
 * @author Aleksandrs.Severgins | <a href="http://alpinweiss.eu">SIA Alpinweiss</a>
 *
 */
@Singleton
public class XmlConfigParserImpl implements XmlConfigParser {

	public static final String REQUIRED = "required";
	public static final String DESCRIPTION = "description";
	public static final String CLASS_NAME = "className";
	public static final String COMMAND = "command";
	public static final String STEP = "step";
    public static final String PARAM_COUNT = "paramCount";
    public static final String REQUIRED_PARAMS = "requiredParams";

	@Override
	public Set<FdrStep> getFdrSteps(String fileName) {
		Set<FdrStep> fdrStepSet = new HashSet<FdrStep>();

        InputStream resourceAsStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);

        try {
            DocumentBuilder builder =
					DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc = builder.parse(resourceAsStream);

			NodeList nodes = doc.getElementsByTagName(STEP);
			for (int i = 0; i < nodes.getLength(); i++) {
				FdrStep step = new FdrStep();
				Element element = (Element) nodes.item(i);

				NodeList title = element.getElementsByTagName(COMMAND);
				Element line = (Element) title.item(0);

				String command = getCharacterDataFromElement(line);
				step.setCommand(command);

				NodeList url = element.getElementsByTagName(CLASS_NAME);
				line = (Element) url.item(0);
				String className = getCharacterDataFromElement(line);
				step.setClassName(className);

				NodeList description = element.getElementsByTagName(DESCRIPTION);
				line = (Element) description.item(0);
				String descriptionStr = getCharacterDataFromElement(line);
				step.setDescription(descriptionStr);

				NodeList requiredElement = element.getElementsByTagName(REQUIRED);
				line = (Element) requiredElement.item(0);
				String boolStr = getCharacterDataFromElement(line);
				Boolean required = new Boolean(boolStr);
				step.setRequired(required);

                NodeList param = element.getElementsByTagName(PARAM_COUNT);
				line = (Element) param.item(0);
				String paramCountStr = getCharacterDataFromElement(line);
				Integer parameter = new Integer(paramCountStr);
				step.setParameters(parameter);

                NodeList requiredParam = element.getElementsByTagName(REQUIRED_PARAMS);
				line = (Element) requiredParam.item(0);
				String requiredParamCountStr = getCharacterDataFromElement(line);
				Integer requiredParameter = new Integer(requiredParamCountStr);
				step.setParameters(requiredParameter);

                fdrStepSet.add(step);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return fdrStepSet;
	}

	private String getCharacterDataFromElement(Element e) {
		Node child = e.getFirstChild();
		if (child instanceof CharacterData) {
			CharacterData cd = (CharacterData) child;
			return cd.getData();
		}
		return "?";
	}

}
