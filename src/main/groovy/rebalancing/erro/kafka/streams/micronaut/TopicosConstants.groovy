package rebalancing.erro.kafka.streams.micronaut

import groovy.transform.CompileStatic


@CompileStatic
abstract class TopicosConstants {

	public static final String PORTAL_CLIENTE = 'portal.public.cliente'
	public static final String PORTAL_GRUPO_CLIENTES = 'portal.public.grupo_clientes'
	public static final String PORTAL_GRUPO_OPERADORAS = 'portal.public.grupo_operadoras'
	public static final String PORTAL_OPERADORA_ANS = 'portal.public.operadora_ans'
	public static final String PORTAL_OPERADORA_MULTITENANT = 'portal.public.operadora_multitenant'

}
