import asyncio
from imjoy_rpc.hypha import connect_to_server
import ssl
import threading


# Patch the default context to disable certificate verification
_create_default_context = ssl.create_default_context

def patched_create_default_context():
    ctx = _create_default_context()
    ctx.check_hostname = False
    ctx.verify_mode = ssl.CERT_NONE
    return ctx

ssl.create_default_context = patched_create_default_context


async def start_server(server_url, server_ready):
    try:
        api = await connect_to_server({"server_url": server_url})
        
        def hello(name):
            print("Hello " + name)
            return "Hello " + name

        await api.register_service({
            "name": "Hello World",
            "id": "hello-world",
            "config": {
                "visibility": "public"
            },
            "hello": hello,
        })
        
        print(f"hello world service regisered at workspace: {api.config.workspace}")
        print(f"Test it with the http proxy: {server_url}/{api.config.workspace}/services/hello-world/hello?name=John")
    except Exception as exp:
        server_ready.set_exception(exp)
    else:
        server_ready.set_result(api)

async def register_function(id, func, example_arg):
    api = await server_ready
    await api.register_service({
        "name": id,
        "id": id,
        "config": {
            "visibility": "public"
        },
        "run": lambda x: func(x)
    })
    print(f"Visiting {server_url}/{api.config.workspace}/services/{id}/run?x={example_arg}")

async def timer_print():
    while True:
        print("Timer")
        await asyncio.sleep(1)

def init_worker(loop, server_url):
    asyncio.set_event_loop(loop)
    global server_ready
    server_ready = asyncio.Future()
    loop.create_task(start_server(server_url, server_ready))
    # loop.create_task(timer_print())
    loop.run_forever()

if __name__ == "__main__":
    server_url = "https://ai.imjoy.io"
    # loop = asyncio.new_event_loop()
    # loop = asyncio.get_event_loop()
    # init_worker(loop, server_url)

    loop = asyncio.new_event_loop()
    worker = threading.Thread(target=init_worker, args=(loop, server_url))
    worker.daemon = True
    worker.start()
    # worker.join()