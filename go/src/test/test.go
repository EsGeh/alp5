package main

import p "process"
import (
	"fmt"
	"time"
)


type EchoProcess struct {
	mailbox p.Process
	out p.Process
}

var countProc = 5

type Signal struct {}

func main() {
	/*test := NewEchoProcess()

	go test.mailbox.Send("test")
	go fmt.Println( test.mailbox.Recv() )*/

	var procs []EchoProcess
	procs = make( []EchoProcess, countProc )
	fmt.Println("creating nodes...")
	for i:=0; i<countProc; i++ {
		procs[i] = *NewEchoProcess()
	}
	fmt.Println("chain them to form a ring...")
	for i:=0; i<countProc; i++ {
		procs[i].setNext( procs[ (i+1) % countProc ] )
	}
	fmt.Println("run them...")
	for i:=0; i<countProc; i++ {
		go procs[i].run()
	}
	procs[0].mailbox.Send("Hallo")

	//wait 1 second:
	sync := make( chan Signal )
	go func () {
		time.Sleep(1 * time.Second)
		sync <- Signal{}
	} ()

	<- sync
	fmt.Println("done")
}

func NewEchoProcess() (proc *EchoProcess) {
	mailbox := p.New("", 0)
	proc = &EchoProcess{
		mailbox,
		nil }
	return
}

func (this *EchoProcess) setNext(next EchoProcess) {
	this.out = next.mailbox
}

func (this *EchoProcess) run() {
	fmt.Println(" started node ")
	for {
		msg := this.mailbox.Recv()
		fmt.Println("message received: ", msg)
		time.Sleep(100 * time.Millisecond)
		this.out.Send( msg )
		if msg == "exit" {
			break
		}
	}
}
