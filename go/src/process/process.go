package process


type Any interface{}

type Process struct {
	mailbox chan Any
	peers []Process
}

func New( name string, size uint) (proc *Process) {
	mailbox := make(chan Any, size)
	proc = & Process{ 
		mailbox,
		nil,
	}
	return
}

func (this *Process) getPeers() ([]Process) {
	return this.peers
}

func (this* Process) start(peers []Process) {
	this.peers = peers
}


func (this* Process) send(message Any) {
	this.mailbox <- message
}

func (this* Process) recv() Any {
	return <- this.mailbox
}
