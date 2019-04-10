package com.eresearch.dblp.consumer.connector;


import com.eresearch.dblp.consumer.dto.DblpConsumerDto;
import com.eresearch.dblp.consumer.dto.dblp.author.DblpAuthor;
import com.eresearch.dblp.consumer.dto.dblp.entry.generated.Dblp;
import com.eresearch.dblp.consumer.exception.BusinessProcessingException;

import java.util.List;
import java.util.Map;

public interface DblpSearchConnector {

    Map<DblpAuthor, List<Dblp>> searchDblp(DblpConsumerDto dblpConsumerDto) throws BusinessProcessingException;
}
