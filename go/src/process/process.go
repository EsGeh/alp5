package process


type Process interface{
	getPeers() []Process
	start(peers []Process)
	send(message Any)
}

type Any interface{}

type ProcessImpl struct {
	mailbox chan Any
	peers []Process
}

func New( name string, size uint) (proc *ProcessImpl) {
	mailbox := make(chan Any, size)
	proc = & ProcessImpl{ 
		mailbox,
		nil,
	}
	return
}

func (this *ProcessImpl) getPeers() ([]Process) {
	return this.peers
}

func (this* ProcessImpl) start(peers []Process) {
	this.peers = peers
}


func (this* ProcessImpl) send(message Any) {
	this.mailbox <- message
}

func (this* ProcessImpl) recv() Any {
	return <- this.mailbox
}
