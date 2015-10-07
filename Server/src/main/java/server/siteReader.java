package server;

/**
 * @author David david.bajko@senacor.com
 */

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

class SiteReader {
    private static Logger logger = Logger.getLogger(SiteReader.class);
    private static String queueName = "ActiveMQ.Statistics.Destination.test.queue";
    private static String ACTIVEMQ_URL = "http://localhost:8161/admin/queues.jsp";

    public Map<String, Integer> getQueueInfo() throws Exception {
        Document document = Jsoup.connect(ACTIVEMQ_URL).get();

        Elements selectedElements = document.select(".content_r div table #queues tbody tr td");

        Iterator<Element> elementIterator = selectedElements.iterator();
        for(Element element : selectedElements){
            if(elementIterator.hasNext()){
                elementIterator.next();
            }

            if(element.text().equals(queueName)){
                Map<String, Integer> queueValues = new HashMap<String, Integer>();
                queueValues.put("Pending Messages",Integer.valueOf(elementIterator.next().text().toString()));
                queueValues.put("Consumers",Integer.valueOf(elementIterator.next().text().toString()));
                queueValues.put("Enqueued",Integer.valueOf(elementIterator.next().text().toString()));
                queueValues.put("Dequeued",Integer.valueOf(elementIterator.next().text().toString()));

                return queueValues;

            }
        }
        return null;
    }
}
