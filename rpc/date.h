/*
 * Please do not edit this file.
 * It was generated using rpcgen.
 */

#ifndef _DATE_H_RPCGEN
#define _DATE_H_RPCGEN

#define RPCGEN_VERSION	199506

#include <rpc/rpc.h>


#define DATE_PROG ((rpc_uint)0x31234567)
#define DATE_VERS ((rpc_uint)1)

#ifdef __cplusplus
#define DATE ((rpc_uint)1)
extern "C" char ** date_1(long *, CLIENT *);
extern "C" char ** date_1_svc(long *, struct svc_req *);
#define MEMORY ((rpc_uint)2)
extern "C" double * memory_1(void *, CLIENT *);
extern "C" double * memory_1_svc(void *, struct svc_req *);
#define PROCESS ((rpc_uint)3)
extern "C" double * process_1(void *, CLIENT *);
extern "C" double * process_1_svc(void *, struct svc_req *);
#define CPU ((rpc_uint)4)
extern "C" double * cpu_1(void *, CLIENT *);
extern "C" double * cpu_1_svc(void *, struct svc_req *);

#elif __STDC__
#define DATE ((rpc_uint)1)
extern  char ** date_1(long *, CLIENT *);
extern  char ** date_1_svc(long *, struct svc_req *);
#define MEMORY ((rpc_uint)2)
extern  double * memory_1(void *, CLIENT *);
extern  double * memory_1_svc(void *, struct svc_req *);
#define PROCESS ((rpc_uint)3)
extern  double * process_1(void *, CLIENT *);
extern  double * process_1_svc(void *, struct svc_req *);
#define CPU ((rpc_uint)4)
extern  double * cpu_1(void *, CLIENT *);
extern  double * cpu_1_svc(void *, struct svc_req *);

#else /* Old Style C */
#define DATE ((rpc_uint)1)
extern  char ** date_1();
extern  char ** date_1_svc();
#define MEMORY ((rpc_uint)2)
extern  double * memory_1();
extern  double * memory_1_svc();
#define PROCESS ((rpc_uint)3)
extern  double * process_1();
extern  double * process_1_svc();
#define CPU ((rpc_uint)4)
extern  double * cpu_1();
extern  double * cpu_1_svc();
#endif /* Old Style C */

#endif /* !_DATE_H_RPCGEN */
